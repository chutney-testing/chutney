package com.chutneytesting.idea.vcs

import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory

class ChutneyCheckinHandlerFactory : CheckinHandlerFactory() {
    override fun createHandler(checkinProjectPanel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        return ChutneyCheckinHandler(checkinProjectPanel)
    }
}
