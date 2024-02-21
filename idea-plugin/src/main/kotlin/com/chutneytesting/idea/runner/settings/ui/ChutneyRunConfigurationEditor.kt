package com.chutneytesting.idea.runner.settings.ui

import com.chutneytesting.idea.runner.ChutneyRunConfiguration
import com.chutneytesting.idea.runner.TestType
import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.UIUtil
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.*
import javax.swing.*


class ChutneyRunConfigurationEditor(val project: Project) : SettingsEditor<ChutneyRunConfiguration>() {


    private var myTabSections: MutableMap<String, RunSettingsSection>? = null
    private var myRootComponent: JComponent? = null

    init {
        myTabSections = mutableMapOf<String, RunSettingsSection>()
        myTabSections!!["Configuration"] = RootSection()
        myTabSections!!["Server"] = ChutneyRunConfigurationServerSection()
        //myTabSections.put("Coverage", ChutneyCoverageSection(project))
        myRootComponent = createTabbedPane(project, myTabSections!!)
    }

    private fun createTabbedPane(
        project: Project,
        tabs: MutableMap<String, RunSettingsSection>
    ): JBTabbedPane {
        val tabbedPane = JBTabbedPane()
        val content = CreationContext(project)
        for ((key, value) in tabs) {
            val component = value.getComponent(content)
            tabbedPane.addTab(key, component)
        }
        tabbedPane.selectedIndex = 0
        return tabbedPane
    }

    override fun resetEditorFrom(runConfiguration: ChutneyRunConfiguration) {
        val runSettings = runConfiguration.getRunSettings()
        for (section in myTabSections!!.values) {
            section.resetFrom(runSettings)
        }
    }

    override fun applyEditorTo(runConfiguration: ChutneyRunConfiguration) {
        val builder = ChutneyRunSettings()
        for (section in myTabSections!!.values) {
            section.applyTo(builder)
        }
        runConfiguration.setRunSettings(builder)

    }

    override fun createEditor(): JComponent = myRootComponent!!

}

private class RootSection : AbstractRunSettingsSection() {

    private var myTestTypeComboBox: JComboBox<*>? = null
    private var myTestTypeContentRunSettingsSection: OneOfRunSettingsSection<TestTypeListItem>? = null
    private var myListItemByTestTypeMap: Map<TestType, TestTypeListItem>? = null
    private val myLabel = JBLabel("Test:")

    private val selectedTestType: TestType
        get() = (myTestTypeComboBox!!.selectedItem as TestTypeListItem).testType


    override fun createComponent(creationContext: CreationContext): JComponent {
        val panel = JPanel(GridBagLayout())
        myLabel.horizontalAlignment = SwingConstants.RIGHT
        panel.add(
            myLabel, GridBagConstraints(
                0, 0,
                1, 1,
                0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                Insets(0, 0, 0, UIUtil.DEFAULT_HGAP),
                0, 0
            )
        )

        val testTypeListItems = createTestTypeListItems(creationContext.project)

        myTestTypeComboBox = createTestTypeComboBox(testTypeListItems)
        panel.add(
            myTestTypeComboBox, GridBagConstraints(
                1, 0,
                1, 1,
                0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                Insets(0, 0, 0, 0),
                0, 0
            )
        )
        myListItemByTestTypeMap = createListItemByTestTypeMap(testTypeListItems)

        myTestTypeContentRunSettingsSection = OneOfRunSettingsSection(testTypeListItems)
        panel.add(
            myTestTypeContentRunSettingsSection!!.getComponent(creationContext), GridBagConstraints(
                0, 1,
                2, 1,
                1.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                Insets(0, 0, 0, 0),
                0, 0
            )
        )

        anchor = myTestTypeContentRunSettingsSection!!.anchor
        return panel
    }

    override fun resetFrom(runSettings: ChutneyRunSettings) {
        selectTestType(runSettings.testType)
        myTestTypeContentRunSettingsSection!!.resetFrom(runSettings)
    }

    override fun applyTo(runSettings: ChutneyRunSettings) {
        val selectedTestType = selectedTestType
        selectTestType(selectedTestType)
        runSettings.testType = selectedTestType
        myTestTypeContentRunSettingsSection!!.applyTo(runSettings)
    }


    private fun createTestTypeListItems(project: Project): List<TestTypeListItem> {
        return Arrays.asList(
            TestTypeListItem(
                TestType.ALL_SCENARIO_IN_DIRECTORY,
                "All scenario files in directory",
                AllInDirectoryRunSettingsSection()
            ),
            TestTypeListItem(TestType.SCENARIO_FILE, "Scenario file", ScenarioFileRunSettingsSection()),
            TestTypeListItem(
                TestType.MUTLI_SCENARIO_FILES,
                "Multiple scenario files",
                ScenariosRunSettingsSection(project)
            )
        )
    }


    private fun createListItemByTestTypeMap(
        testTypeListItems: List<TestTypeListItem>
    ): Map<TestType, TestTypeListItem> {
        val map = EnumMap<TestType, TestTypeListItem>(TestType::class.java)
        for (testTypeListItem in testTypeListItems) {
            map[testTypeListItem.testType] = testTypeListItem
        }
        return map
    }


    private fun createTestTypeComboBox(testTypeListItems: List<TestTypeListItem>): JComboBox<*> {
        val comboBox = ComboBox(testTypeListItems.toTypedArray())
        comboBox.renderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel

                if (value is TestTypeListItem) {
                    label.text =  value.displayName
                }

                return label
            }
        }
        comboBox.addActionListener { selectTestType(selectedTestType) }
        return comboBox
    }

    private fun selectTestType(testType: TestType) {
        val testTypeListItem = myListItemByTestTypeMap!![testType]
        val comboBoxModel = myTestTypeComboBox!!.model
        if (comboBoxModel.selectedItem !== testTypeListItem) {
            comboBoxModel.selectedItem = testTypeListItem
        }
        if (testTypeListItem != null) {
            myTestTypeContentRunSettingsSection!!.select(testTypeListItem)
        }
    }

    override fun setAnchor(anchor: JComponent?) {
        super.setAnchor(anchor)
        myTestTypeContentRunSettingsSection!!.anchor = anchor
        myLabel.anchor = anchor
    }
}
