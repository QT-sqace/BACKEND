node {
    def branchName = env.BRANCH_NAME
    if (branchName == 'gateway') {
        checkout scm
        dir('gateway') {
            load 'Jenkinsfile'
        }
    } else if (branchName == 'jira-auth-service') {
        checkout scm
        dir('jira-link') {
            load 'Jenkinsfile'
        }
    } else {
        echo "No specific Jenkinsfile for this branch"
    }
}