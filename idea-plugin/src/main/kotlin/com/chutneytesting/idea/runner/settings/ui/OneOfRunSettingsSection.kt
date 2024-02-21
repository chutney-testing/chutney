package com.chutneytesting.idea.runner.settings.ui

import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.intellij.util.ui.UIUtil
import java.awt.CardLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * All methods should be executed on EDT.
 */
class OneOfRunSettingsSection<T> internal constructor(private val myRunSettingsSectionProviders: Collection<T>) :
    AbstractRunSettingsSection() where T : IdProvider, T : RunSettingsSectionProvider {

    private val myCardPanel: JPanel = JPanel(CardLayout())
    private val mySectionByIdMap = mutableMapOf<String, RunSettingsSection>()
    private var mySelectedKey: T? = null

    private val selectedRunSettingsSection: RunSettingsSection?
        get() = mySectionByIdMap[mySelectedKey!!.id]

    override fun resetFrom(runSettings: ChutneyRunSettings) {
        for (runSettingsSection in mySectionByIdMap.values) {
            runSettingsSection.resetFrom(runSettings)
        }
    }

    override fun applyTo(runSettings: ChutneyRunSettings) {
        val runSettingsSection = selectedRunSettingsSection
        runSettingsSection?.applyTo(runSettings)
    }

    override fun createComponent(creationContext: CreationContext): JPanel {
        for (child in myRunSettingsSectionProviders) {
            val runSettingsSection = child.provideRunSettingsSection()
            val comp = runSettingsSection.getComponent(creationContext)
            myCardPanel.add(comp, child.id)
            mySectionByIdMap[child.id] = runSettingsSection
        }
        val iterator = myRunSettingsSectionProviders.iterator()
        if (iterator.hasNext()) {
            select(iterator.next())
        } else {
            throw RuntimeException("No child items were found")
        }
        anchor = UIUtil.mergeComponentsWithAnchor(mySectionByIdMap.values)
        return myCardPanel
    }

    fun select(key: T) {
        if (mySelectedKey !== key) {
            val cardLayout = myCardPanel.layout as CardLayout
            cardLayout.show(myCardPanel, key.id)
            mySelectedKey = key
        }
    }

    override fun setAnchor(anchor: JComponent?) {
        super.setAnchor(anchor)
        for (runSettingsSection in mySectionByIdMap.values) {
            runSettingsSection.anchor = anchor
        }
    }
}
