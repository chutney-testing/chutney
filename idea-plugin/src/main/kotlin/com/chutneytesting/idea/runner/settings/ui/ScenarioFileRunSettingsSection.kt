package com.chutneytesting.idea.runner.settings.ui

import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.chutneytesting.idea.util.SwingUtils.addGreedyBottomRow
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileTypeDescriptor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ObjectUtils
import com.intellij.util.ui.UIUtil
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingConstants

class ScenarioFileRunSettingsSection internal constructor() : AbstractRunSettingsSection() {
    //private final ConfigFileRunSettingsSection myConfigFileRunSettingsSection;
    private val myScenarioFileLabel: JBLabel
    private val myScenarioFileTextFieldWithBrowseButton: TextFieldWithBrowseButton
    private val myChutneyVariablesLabel: JBLabel
    private val myChutneyVariablesTextFieldWithBrowseButton: ChutneyVariablesTextFieldWithBrowseButton
    val jsTestFileTextField: JTextField
        get() = myScenarioFileTextFieldWithBrowseButton.textField

    override fun resetFrom(runSettings: ChutneyRunSettings) { //myConfigFileRunSettingsSection.resetFrom(runSettings);
        myScenarioFileTextFieldWithBrowseButton.text = runSettings.scenarioFilePath
        myChutneyVariablesTextFieldWithBrowseButton.data = runSettings.variables
    }

    override fun applyTo(runSettingsBuilder: ChutneyRunSettings) { //myConfigFileRunSettingsSection.applyTo(runSettingsBuilder);
        runSettingsBuilder.scenarioFilePath =
            ObjectUtils.notNull(myScenarioFileTextFieldWithBrowseButton.text, "")
        runSettingsBuilder.variables = myChutneyVariablesTextFieldWithBrowseButton.data
    }

    public override fun createComponent(creationContext: CreationContext): JComponent {
        val panel = JPanel(GridBagLayout())
        run {
            val c = GridBagConstraints(
                0, 0,
                2, 1,
                1.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                Insets(0, 0, 0, 0),
                0, 0
            )
        }
        run {
            myScenarioFileLabel.horizontalAlignment = SwingConstants.RIGHT
            myScenarioFileLabel.setDisplayedMnemonic('J')
            myScenarioFileLabel.labelFor = myScenarioFileTextFieldWithBrowseButton.textField
            val c = GridBagConstraints(
                0, 1,
                1, 1,
                0.0, 0.0,
                GridBagConstraints.EAST,
                GridBagConstraints.NONE,
                Insets(UIUtil.DEFAULT_VGAP, 0, 0, UIUtil.DEFAULT_HGAP),
                0, 0
            )
            panel.add(myScenarioFileLabel, c)
        }
        run {
            val c = GridBagConstraints(
                1, 1,
                1, 1,
                1.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                Insets(UIUtil.DEFAULT_VGAP, 0, 0, 0),
                0, 0
            )
            val scenarioFileChooserDescriptor: FileChooserDescriptor =
                FileTypeDescriptor("Select Chutney Scenario file", ".json")
            myScenarioFileTextFieldWithBrowseButton.addBrowseFolderListener(
                null,
                null,
                creationContext.project,
                scenarioFileChooserDescriptor
            )
            panel.add(myScenarioFileTextFieldWithBrowseButton, c)
            myScenarioFileLabel.labelFor = myScenarioFileTextFieldWithBrowseButton
        }
        //Variables
        run {
            myChutneyVariablesLabel.horizontalAlignment = SwingConstants.RIGHT
            myChutneyVariablesLabel.setDisplayedMnemonic('J')
            myChutneyVariablesLabel.labelFor = myChutneyVariablesTextFieldWithBrowseButton.textField
            val c = GridBagConstraints(
                0, 2,
                1, 1,
                0.0, 0.0,
                GridBagConstraints.EAST,
                GridBagConstraints.NONE,
                Insets(UIUtil.DEFAULT_VGAP, 0, 0, UIUtil.DEFAULT_HGAP),
                0, 0
            )
            panel.add(myChutneyVariablesLabel, c)
        }
        run {
            val c = GridBagConstraints(
                1, 2,
                1, 1,
                1.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                Insets(UIUtil.DEFAULT_VGAP, 0, 0, 0),
                0, 0
            )
            panel.add(myChutneyVariablesTextFieldWithBrowseButton, c)
            myChutneyVariablesLabel.labelFor = myChutneyVariablesTextFieldWithBrowseButton
        }
        addGreedyBottomRow(panel)
        return panel
    }

    override fun setAnchor(anchor: JComponent?) {
        super.setAnchor(anchor)
        myScenarioFileLabel.anchor = anchor
        //myConfigFileRunSettingsSection.setAnchor(anchor);
    }

    init { //myConfigFileRunSettingsSection = new ConfigFileRunSettingsSection();
        myScenarioFileTextFieldWithBrowseButton = TextFieldWithBrowseButton()
        myScenarioFileLabel = JBLabel("Scenario file:")
        myChutneyVariablesLabel = JBLabel("Variables:")
        myChutneyVariablesTextFieldWithBrowseButton = ChutneyVariablesTextFieldWithBrowseButton()
        //setAnchor(SwingUtils.getWiderComponent(myScenarioFileLabel, myConfigFileRunSettingsSection));
    }
}
