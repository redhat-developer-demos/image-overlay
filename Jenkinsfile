pipeline {
  agent {
    dockerfile {
      filename 'Dockerfile'
    }

  }
  stages {
    stage('') {
      steps {
        withMaven() {
          build 'mvn clean package'
        }

      }
    }
  }
}