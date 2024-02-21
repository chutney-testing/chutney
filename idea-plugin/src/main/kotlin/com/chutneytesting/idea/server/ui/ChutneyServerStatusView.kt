package com.chutneytesting.idea.server.ui

import com.chutneytesting.idea.server.ChutneyBrowserInfo
import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerLifeCycleAdapter
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.util.Ref
import com.intellij.ui.PopupHandler
import com.intellij.ui.SideBorder
import com.intellij.util.PlatformIcons
import com.intellij.util.ui.SwingHelper
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.Component
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import javax.swing.*
import javax.swing.event.HyperlinkEvent

class ChutneyServerStatusView(private val myDisposable: Disposable) {
    private val myInfoPane: JEditorPane
    private val myBrowsersLabel: JLabel
    private val myRoot: JPanel
    val component: JComponent
        get() = myRoot

    fun attachToServer(server: ChutneyServer) {
        setInfoBodyHtml("Starting up...")
        server.addLifeCycleListener(object : ChutneyServerLifeCycleAdapter() {
            override fun onServerStarted() {
                val settings = server.settings
                // TODO: 18/12/2018 cleanup
                val url = "http://localhost:" + settings.port + "/"
                //setInfoBodyHtml("To capture a browser open <a href='" + url + "'>" + url + "</a>");
                setInfoBodyHtml("Chutney server started at <a href='$url'>$url</a>")
                //updateCapturedBrowsersCount(server);
            }

            override fun onServerStopped() {
                setInfoBodyHtml("Not running")
                updateCapturedBrowsersCount(server)
            }

            override fun onBrowserCaptured(info: ChutneyBrowserInfo) {
                updateCapturedBrowsersCount(server)
            }

            override fun onBrowserPanicked(info: ChutneyBrowserInfo) {
                updateCapturedBrowsersCount(server)
            }
        }, myDisposable)
    }

    private fun updateCapturedBrowsersCount(server: ChutneyServer) {
        val capturedBrowsers: Int
        capturedBrowsers = if (server.isStopped) {
            0
        } else {
            server.capturedBrowsers.size
        }
        // TODO: 18/12/2018 cleanup
//myBrowsersLabel.setText("Captured browsers: " + capturedBrowsers);
    }

    private fun setInfoBodyHtml(htmlBody: String) {
        val styleTag = UIUtil.getCssFontDeclaration(UIUtil.getLabelFont())
        myInfoPane.text = "<html>$styleTag<body>$htmlBody</body></html>"
    }

    private class OpenLinkInBrowser internal constructor() :
        AnAction("Open Link in Browser", null, PlatformIcons.WEB_ICON) {
        private var myUrl: String? = null
        override fun update(e: AnActionEvent) {
            e.presentation.isEnabledAndVisible = myUrl != null
        }

        override fun actionPerformed(e: AnActionEvent) {
            val url = myUrl
            if (url != null) {
                BrowserUtil.browse(url)
            }
        }

        fun setUrl(url: String?) {
            myUrl = url
        }
    }

    private class CopyLinkAction internal constructor() : AnAction("Copy Link Address", null, PlatformIcons.COPY_ICON) {
        private var myUrl: String? = null
        override fun update(e: AnActionEvent) {
            e.presentation.isEnabledAndVisible = myUrl != null
        }

        override fun actionPerformed(e: AnActionEvent) {
            val url = myUrl
            if (url != null) {
                val content: Transferable = StringSelection(url)
                CopyPasteManager.getInstance().setContents(content)
            }
        }

        fun setUrl(url: String?) {
            myUrl = url
        }
    }

    companion object {
        private fun createInfoPane(): JEditorPane {
            val textPane = JEditorPane()
            textPane.font = UIUtil.getLabelFont()
            textPane.contentType = UIUtil.HTML_MIME
            textPane.isEditable = false
            textPane.isOpaque = false
            textPane.background = UIUtil.TRANSPARENT_COLOR
            installLinkHandler(textPane)
            return textPane
        }

        private fun installLinkHandler(pane: JEditorPane) {
            val urlRef = Ref.create<String>(null)
            pane.addHyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.EXITED) {
                    urlRef.set(null)
                } else if (e.eventType == HyperlinkEvent.EventType.ENTERED) {
                    urlRef.set(e.description)
                } else if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    BrowserUtil.browse(e.description)
                }
            }
            val copyLinkAction = CopyLinkAction()
            val openLinkInBrowserAction = OpenLinkInBrowser()
            val group = DefaultActionGroup(openLinkInBrowserAction, copyLinkAction)
            pane.addMouseListener(object : PopupHandler() {
                override fun invokePopup(comp: Component, x: Int, y: Int) {
                    val url = urlRef.get()
                    copyLinkAction.setUrl(url)
                    openLinkInBrowserAction.setUrl(url)
                    if (url != null) {
                        val popupMenu = ActionManager.getInstance().createActionPopupMenu(ActionPlaces.UNKNOWN, group)
                        popupMenu.component.show(comp, x, y)
                    }
                }
            })
        }
    }

    init {
        myInfoPane = createInfoPane()
        myBrowsersLabel = JLabel()
        val panel = SwingHelper.newHorizontalPanel(
            Component.CENTER_ALIGNMENT,
            myInfoPane,
            Box.createHorizontalGlue(),
            myBrowsersLabel
        )
        panel.border = BorderFactory.createEmptyBorder(3, 7, 5, 12)
        val wrap = JPanel(BorderLayout(0, 0))
        wrap.add(panel, BorderLayout.CENTER)
        wrap.border = SideBorder(UIUtil.getBoundsColor(), SideBorder.BOTTOM)
        myRoot = wrap
    }
}
