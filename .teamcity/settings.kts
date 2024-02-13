import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.buildSteps.python
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerRegistry
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
    vcsRoot(HttpsGithubComWojdakSampleAppGitRefsHeadsMain)

    buildType(Build)
    buildType(HelloWorld)

    features {
        dockerRegistry {
            id = "PROJECT_EXT_3"
            name = "Docker Registry"
            userName = "Wojdak"
            password = "credentialsJSON:2b482261-e979-4a3f-bc29-24aa23d840a8"
        }
    }
}

object Build : BuildType({
    name = "Build Flask App"

    vcs {
        root(HttpsGithubComWojdakSampleAppGitRefsHeadsMain)
    }

    steps {
        python {
            name = "Run tests"
            id = "python_runner"
            enabled = false
            command = pytest {
            }
        }
        dockerCommand {
            name = "Build docker image"
            id = "DockerCommand"
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                namesAndTags = "wojdak/my-app:latest"
            }
        }
        dockerCommand {
            name = "Push image to Docker Hub"
            id = "Push_image_to_Docker_Hub"
            commandType = push {
                namesAndTags = "wojdak/my-app:latest"
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_3"
            }
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

object HttpsGithubComWojdakSampleAppGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/Wojdak/sample-app.git#refs/heads/main"
    url = "https://github.com/Wojdak/sample-app.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "Wojdak"
        password = "credentialsJSON:fa36a0d7-2e4f-4ffd-a808-e1abeaa7dc90"
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
