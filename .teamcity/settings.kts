import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    vcsRoot(HttpsGithubComWojdakSampleAppRefsHeadsMain)

    buildType(BuildFlaskApp)
    buildType(HelloWorld)
}

object BuildFlaskApp : BuildType({
    name = "Build Flask App"

    vcs {
        root(HttpsGithubComWojdakSampleAppRefsHeadsMain)
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object HelloWorld : BuildType({
    name = "Hello world"

    steps {
        script {
            scriptContent = "echo 'Hello world!'"
        }
    }
})

object HttpsGithubComWojdakSampleAppRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/Wojdak/sample-app#refs/heads/main"
    url = "https://github.com/Wojdak/sample-app"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "Wojdak"
        password = "credentialsJSON:fa36a0d7-2e4f-4ffd-a808-e1abeaa7dc90"
    }
})
