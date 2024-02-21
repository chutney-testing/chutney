package com.chutneytesting.idea.util

import com.intellij.ui.PanelWithAnchor
import com.intellij.util.ObjectUtils
import com.intellij.util.ui.UIUtil
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.Insets
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

object SwingUtils {
    fun addTextChangeListener(textComponent: JTextComponent, textChangeListener: TextChangeListener) {
        val oldTextContainer = arrayOf(textComponent.text)
        textChangeListener.textChanged("", textComponent.text)
        textComponent.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                textChanged()
            }

            override fun removeUpdate(e: DocumentEvent) {
                textChanged()
            }

            override fun changedUpdate(e: DocumentEvent) {
                textChanged()
            }

            fun textChanged() {
                val oldText = ObjectUtils.notNull(oldTextContainer[0], "")
                val newText = ObjectUtils.notNull(textComponent.text, "")
                if (oldText != newText) {
                    textChangeListener.textChanged(oldText, newText)
                    oldTextContainer[0] = newText
                }
            }
        })
    }

    private fun convertIconToBufferedImage(icon: Icon): BufferedImage { // We can't use here UIUtil.createImage(), because of WEB-7013
        val image = BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        graphics.color = UIUtil.TRANSPARENT_COLOR
        graphics.fillRect(0, 0, icon.iconWidth, icon.iconHeight)
        icon.paintIcon(null, graphics, 0, 0)
        graphics.dispose()
        return image
    }

    fun getGreyIcon(icon: Icon): Icon {
        val op = ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
        val originalImage = convertIconToBufferedImage(icon)
        val greyImage = op.filter(originalImage, null)
        return ImageIcon(greyImage)
    }

    @JvmStatic
    fun addGreedyBottomRow(gridBagPanel: JPanel) {
        val c = GridBagConstraints(
            0, GridBagConstraints.RELATIVE,
            GridBagConstraints.REMAINDER, 1,
            1.0, 1.0,
            GridBagConstraints.CENTER,
            GridBagConstraints.BOTH,
            Insets(0, 0, 0, 0),
            0, 0
        )
        gridBagPanel.add(JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)), c)
    }

    fun getWiderComponent(anchor: JComponent, panelWithAnchor: PanelWithAnchor): JComponent {
        val panelAnchor = panelWithAnchor.anchor ?: return anchor
        return if (panelAnchor.preferredSize.width < anchor.preferredSize.width) {
            anchor
        } else panelAnchor
    }
}
