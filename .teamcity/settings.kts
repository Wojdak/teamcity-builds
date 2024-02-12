import jetbrains.buildServer.configs.kotlin.*

version = "2023.11"

val infraRepo = GitVcsRoot {
    id("Infra_Repo")
    name = "Infrastructure Repository"
    url = "https://github.com/Wojdak/workflow-example"
    branch = "main"
}

project {
    buildType<InfraBuild>()
}

class InfraBuild : BuildType({
    id("InfraBuild_Build")
    name = "Terraform and Ansible Build"
    description = "Build configuration for deploying infrastructure with Terraform and Ansible"

    vcs {
        root(infraRepo)
    }

    steps {
        script {
            name = "Execute Terraform Commands"
            scriptContent = """
                cd terraform
                terraform init
                terraform plan -out=tfplan
                terraform apply -auto-approve tfplan
                terraform output droplet_ip > ../droplet_ip.txt
            """.trimIndent()
        }

        script {
            name = "Execute Ansible Playbook"
            scriptContent = """
                droplet_ip=$(cat ../droplet_ip.txt)
                cd ../ansible
                echo "[droplet]" > inventory.ini
                echo "\$droplet_ip" >> inventory.ini
                ansible-playbook -i inventory.ini deploy.yml --private-key=~/.ssh/id_rsa 
            """.trimIndent()
        }

        script {
            name = "Execute Validation Script"
            scriptContent = """
                droplet_ip=$(cat ../droplet_ip.txt)
                cd ..
                ./validate.sh "\$droplet_ip"
            """.trimIndent()
        }
    }
})
