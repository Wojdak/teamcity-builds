import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.v2023.11.*
import jetbrains.buildServer.configs.kotlin.v2023.11.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2023.11.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2023.11.vcs.GitVcsRoot
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

val infraRepo = GitVcsRoot({
    id("Infra_Repo")
    name("Infrastructure Repository")
    url("https://github.com/Wojdak/workflow-example")
    branch("main")
    // Add authentication credentials if required
})

project {
    buildType(InfraBuild)
}

class InfraBuild : BuildType({
    id("InfraBuild_Build")
    name = "Terraform and Ansible Build"
    description = "Build configuration for deploying infrastructure with Terraform and Ansible"

    // Set up VCS Roots
    vcs {
        root(infraRepo)
    }

    // Define build steps
    steps {
        // Step 1: Execute Terraform commands
        script {
            name = "Execute Terraform Commands"
            scriptContent = """
                cd terraform
                terraform init
                terraform plan -out=tfplan
                terraform apply -auto-approve tfplan
                terraform output droplet_ip > ../droplet_ip.txt
            """
        }

        // Step 2: Execute Ansible playbook
        script {
            name = "Execute Ansible Playbook"
            scriptContent = """
                droplet_ip=$(cat ../droplet_ip.txt)
                cd ../ansible
                echo "[droplet]" > inventory.ini
                echo "$droplet_ip" >> inventory.ini
                ansible-playbook -i inventory.ini deploy.yml --private-key=~/.ssh/id_rsa 
            """
        }

        // Step 3: Execute validation script
        script {
            name = "Execute Validation Script"
            scriptContent = """
                droplet_ip=$(cat ../droplet_ip.txt)
                cd ..
                ./validate.sh "$droplet_ip"
            """
        }
    }
})