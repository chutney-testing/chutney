import com.chutneytesting.environment.domain.EnvironmentService
import com.chutneytesting.environment.domain.exception.AlreadyExistingEnvironmentException
import com.chutneytesting.environment.infra.JsonFilesEnvironmentRepository
import com.chutneytesting.kotlin.execution.CHUTNEY_ENV_ROOT_PATH_DEFAULT
import com.chutneytesting.kotlin.synchronize.ChutneyServerServiceImpl
import com.chutneytesting.kotlin.util.ChutneyServerInfo

/**
 * Synchronise local environments from remote.
 */
class EnvironmentSynchronizeService(
) {


    fun synchroniseLocal(
        serverInfo: ChutneyServerInfo,
        environmentsPath: String = "$CHUTNEY_ENV_ROOT_PATH_DEFAULT/",
        force: Boolean = false
    ) {
        val environmentRepository = JsonFilesEnvironmentRepository(environmentsPath)
        val environmentService = EnvironmentService(environmentRepository)
        ChutneyServerServiceImpl.getEnvironments(serverInfo)
            .forEach {
                try {
                    environmentService.createEnvironment(it.toEnvironment(), force)
                    println("| ${it.name} local environment was synchronized")
                }
                // do nothing when environment exist and force=false
                catch (e: AlreadyExistingEnvironmentException) {
                }
            }

    }

}

