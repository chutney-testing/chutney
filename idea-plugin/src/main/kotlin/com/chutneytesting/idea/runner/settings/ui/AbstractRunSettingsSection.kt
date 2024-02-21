package com.chutneytesting.idea.runner.settings.ui

import javax.swing.JComponent

/**
 * All methods should be called on EDT.
 */
abstract class AbstractRunSettingsSection : RunSettingsSection {

    private var myComponent: JComponent? = null
    private var myAnchor: JComponent? = null

    override fun getComponent(creationContext: CreationContext): JComponent {
        if (myComponent == null) {
            myComponent = createComponent(creationContext)
        }
        return myComponent as JComponent
    }

    protected abstract fun createComponent(creationContext: CreationContext): JComponent

    override fun getAnchor(): JComponent? {
        return myAnchor
    }

    override fun setAnchor(anchor: JComponent?) {
        myAnchor = anchor
    }
}
