package com.acme.gradle

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.TaskContainerScope

open class CmdTask {
  var command: String = ""
  var workingDir: Any = System.getProperty("user.dir")
  var environment: Map<String, String> = emptyMap()
}

inline fun TaskContainerScope.cmdTask(name: String, block: CmdTask.() -> Unit): TaskProvider<Exec> =
  CmdTask().apply(block).let {
    register(name, Exec::class.java) {
      commandLine(it.command.split(" ").toList())
      environment(it.environment)
      workingDir(it.workingDir)
    }
  }

open class ShellCmdTask : CmdTask() {
  var shell: String = "bash"
}

inline fun TaskContainerScope.shellCmdTask(name: String, block: ShellCmdTask.() -> Unit): TaskProvider<Exec> =
  ShellCmdTask().apply(block).let {
    register(name, Exec::class.java) {
      commandLine(listOf(it.shell, "-c", it.command))
      environment(it.environment)
      workingDir(it.workingDir)
    }
  }

class KubectlApplyCommand : ShellCmdTask() {
  var path: String = System.getProperty("user.dir")
  var kubeconfig: String = "${System.getProperty("user.home")}/.kube/config"
}

inline fun TaskContainerScope.kubectlApplyCmdTask(
  name: String,
  block: KubectlApplyCommand.() -> Unit
): TaskProvider<Exec> =
  KubectlApplyCommand().apply(block).let {
    register(name, Exec::class.java) {
      commandLine(listOf(it.shell, "-c", "kubectl kustomize ${it.path} | kubectl apply -f -"))
      environment(it.environment + mapOf(
        "KUBECONFIG" to it.kubeconfig
      ))
      workingDir(it.workingDir)
    }
  }

class AnsiblePlaybookCmdTask : CmdTask() {
  var playbook: String = ""
  var hosts: String = "hosts.ini"
  var extraVars: Map<String, String> = emptyMap()

  fun mapExtraArgs(): String =
    if (extraVars.isEmpty())
      ""
    else
      "--extra-vars \"${extraVars.map { "${it.key}=${it.value}" }.joinToString(" ")}\""
}

inline fun TaskContainerScope.ansibleCmdTask(name: String, block: AnsiblePlaybookCmdTask.() -> Unit) : TaskProvider<Exec> =
  AnsiblePlaybookCmdTask().apply(block).let {
    register(name, Exec::class.java) {
      commandLine(listOf("bash", "-c", "ansible-playbook -i it.hosts ${it.mapExtraArgs()} ${it.playbook}"))
      environment(it.environment)
      workingDir(it.workingDir)
    }
  }
