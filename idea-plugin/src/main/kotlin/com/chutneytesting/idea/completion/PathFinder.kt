package com.chutneytesting.idea.completion

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class PathFinder {
    /*
     * Finds named children, continuing the traversal even if a child is a list. For example, given:
     * items:
     *   - item1: value1
     *   - item2: value2
     *
     * The method would return "item1" and "item2" elements when called with path "$" and "items" PSI element.
     */
    fun findNamedChildren(path: String?, psiElement: PsiElement?): List<PsiNamedElement> {
        val childFilter = Predicate<PsiElement> { child: PsiElement? ->
            child is NavigatablePsiElement &&
                    child !is JsonStringLiteral
        }
        return findChildrenByPathFrom(PathExpression(path!!), psiElement, childFilter)
    }

    /*
     * Finds named children, stopping the traversal if a child is a list. For example, given:
     * items:
     *   - item1: value1
     *   - item2: value2
     *
     * The method would return an empty list when called with path "$" and "items" PSI element.
     */
    fun findDirectNamedChildren(path: String?, psiElement: PsiElement?): List<PsiNamedElement> {
        val childFilter = Predicate<PsiElement> { child: PsiElement? ->
            child is NavigatablePsiElement &&
                    child !is JsonStringLiteral &&
                    child !is JsonArray
        }
        return findChildrenByPathFrom(PathExpression(path!!), psiElement, childFilter)
    }

    fun findByPathFrom(path: String?, psiElement: PsiElement): Optional<PsiElement> {
        val childFilter = Predicate<PsiElement> { child: PsiElement? ->
            child is NavigatablePsiElement &&
                    child !is JsonStringLiteral
        }
        return findByPathFrom(PathExpression(path!!), psiElement, childFilter)
    }

    fun isInsidePath(psiElement: PsiElement?, path: String?): Boolean {
        return isInsidePath(psiElement, PathExpression(path!!))
    }

    private fun isInsidePath(psiElement: PsiElement?, pathExpression: PathExpression): Boolean {
        if (psiElement == null) {
            return false
        }
        val nextNamedParent = getNextNamedParent(psiElement)
        val unescapedTargetKeyName = unescapedName(pathExpression.last())
        if (pathExpression.isAnyKey) {
            return isInsidePath(getNextNamedParent(nextNamedParent!!.parent), pathExpression.beforeLast())
        }
        if (pathExpression.isAnyKeys) {
            return isInsidePath(
                goUpToElementWithParentName(psiElement, pathExpression.secondLast()),
                pathExpression.beforeLast()
            )
        }
        return if (unescapedTargetKeyName == ROOT_PATH) {
            nextNamedParent is PsiFile
        } else unescapedTargetKeyName == nextNamedParent!!.name &&
                (pathExpression.hasOnePath() ||
                        isInsidePath(nextNamedParent.parent, pathExpression.beforeLast()))
    }

    private fun isRoot(psiElement: PsiElement?): Boolean {
        return psiElement != null && psiElement is PsiFile
    }

    private fun goUpToElementWithParentName(psiElement: PsiElement?, keyName: String): PsiNamedElement? {
        if (psiElement == null) {
            return null
        }
        if (psiElement is PsiNamedElement) {
            if (unescapedName(keyName) == psiElement.name) {
                return psiElement
            } else if (keyName == ROOT_PATH) {
                return if (isRoot(psiElement)) psiElement else goUpToElementWithParentName(
                    psiElement.getParent(),
                    keyName
                )
            }
        }
        return goUpToElementWithParentName(psiElement.parent, keyName)
    }

    private fun getNextNamedParent(psiElement: PsiElement?): PsiNamedElement? {
        if (psiElement == null) {
            return null
        }
        if (psiElement is PsiNamedElement) {
            val namedElement = psiElement
            if (namedElement.name != null && !namedElement.name!!.contains(DUMMY_IDENTIFIER)) {
                return namedElement
            }
        }
        return getNextNamedParent(psiElement.parent)
    }

    private fun getNamedChildren(
        psiElement: PsiElement,
        childFilter: Predicate<PsiElement>
    ): List<PsiNamedElement> {
        val children = Arrays.stream(psiElement.children)
            .filter { child: PsiElement? -> child is PsiNamedElement }
            .map { child: PsiElement -> child as PsiNamedElement }
            .collect(Collectors.toList())
        if (children.isEmpty()) {
            val navigatablePsiElement = Arrays.stream(psiElement.children)
                .filter(childFilter)
                .findFirst()
            return if (navigatablePsiElement.isPresent) getNamedChildren(
                navigatablePsiElement.get(),
                childFilter
            ) else java.util.ArrayList()
        }
        return java.util.ArrayList(children)
    }

    private fun findByPathFrom(
        pathExpression: PathExpression,
        psiElement: PsiElement,
        childFilter: Predicate<PsiElement>
    ): Optional<PsiElement> {
        if (pathExpression.isEmpty) {
            return Optional.of(psiElement)
        }
        val currentNodeName = pathExpression.currentPath
        val remainingPathExpression = pathExpression.afterFirst()
        val childByName = getChildByName(psiElement, currentNodeName, childFilter)
        return childByName.flatMap { el: PsiElement -> findByPathFrom(remainingPathExpression, el, childFilter) }
    }

    private fun findChildrenByPathFrom(
        pathExpression: PathExpression,
        psiElement: PsiElement?,
        childFilter: Predicate<PsiElement>
    ): List<PsiNamedElement> {
        if (psiElement == null) {
            return java.util.ArrayList()
        }
        if (pathExpression.isEmpty) {
            return getNamedChildren(psiElement, childFilter)
        }
        val currentNodeName = pathExpression.currentPath
        val remainingPathExpression = pathExpression.afterFirst()
        if ("parent" == currentNodeName) {
            return findChildrenByPathFrom(ROOT_PATH_EXPRESSION, getNextObjectParent(psiElement), childFilter)
        }
        val childByName = getChildByName(psiElement, currentNodeName, childFilter)
        return childByName
            .map { el: PsiElement? -> findChildrenByPathFrom(remainingPathExpression, el, childFilter) }
            .orElseGet { ArrayList() }
    }

    private fun getChildByName(
        psiElement: PsiElement,
        name: String,
        childFilter: Predicate<PsiElement>
    ): Optional<out PsiElement> {
        if (ROOT_PATH == name) {
            return Optional.of(psiElement)
        }
        val children = Arrays.stream(psiElement.children)
            .filter { child: PsiElement? -> child is PsiNamedElement }
            .map { child: PsiElement -> child as PsiNamedElement }
            .collect(Collectors.toList())
        if (children.isEmpty()) {
            val navigatablePsiElement = Arrays.stream(psiElement.children)
                .filter { child: PsiElement? -> child is NavigatablePsiElement }
                .filter { child: PsiElement? -> child !is JsonStringLiteral }
                .findFirst()
            return if (navigatablePsiElement.isPresent) getChildByName(
                navigatablePsiElement.get(),
                name,
                childFilter
            ) else Optional.empty()
        }
        val unescapedName = unescapedName(name)
        return children.stream()
            .filter { child: PsiNamedElement -> unescapedName == child.name }
            .findFirst()
    }

    private fun unescapedName(name: String): String {
        return name.replace("\\.", ".")
    }

    private fun getNextObjectParent(psiElement: PsiElement?): PsiElement? {
        if (psiElement == null) {
            return null
        }
        return if (psiElement is JsonObject ||
            psiElement !is JsonStringLiteral
        ) {
            psiElement
        } else getNextObjectParent(psiElement.getParent())
    }

    companion object {
        private const val DUMMY_IDENTIFIER = "IntellijIdeaRulezzz"
        private const val ROOT_PATH = "$"
        private val ROOT_PATH_EXPRESSION = PathExpression(ROOT_PATH)
    }
}
