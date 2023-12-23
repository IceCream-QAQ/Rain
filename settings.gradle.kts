rootProject.name = "Rain"

fun includeProject(name: String, dir: String? = null) {
    include(name)
    dir?.let { project(name).projectDir = file(it) }
}

includeProject(":function", "tools/function")
