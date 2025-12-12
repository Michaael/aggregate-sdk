package publication

import groovy.util.*

object PomUtils {
    fun addDependency(dependenciesNode: Node, group: String, name: String, version: String, type: String = "", classifier: String? = null) {
        val dependencyNode = dependenciesNode.appendNode("dependency")

        dependencyNode.appendNode("groupId", group)
        dependencyNode.appendNode("artifactId", name)
        dependencyNode.appendNode("version", version)
        dependencyNode.appendNode("scope", "compile")
        if (type.isNotEmpty()) {
            dependencyNode.appendNode("type", type)
        }
        if (classifier != null) {
            dependencyNode.appendNode("classifier", classifier)
        }
    }
}