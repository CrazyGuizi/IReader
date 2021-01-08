package com.ldg.gradleplugin.route

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class RouterPlugin implements Plugin<Project> {

    public static final String PROJECT_COMPILER = "compiler"

    public static final String APT_OPTION_MODULE_NAME = "moduleName"
    public static final String PROJECT_ROUTER = 'router'


    @Override
    void apply(Project project) {
        println '开始我的插件:' + project.name
        def ext = project.rootProject.ext
        Project router = project.rootProject.findProject(PROJECT_ROUTER)
        Project compiler = project.rootProject.findProject(PROJECT_COMPILER)
        println "路由module：" + router
        if (router && compiler) {
//            project.dependencies.add('implementation', router)
            project.dependencies.add('annotationProcessor', compiler)
        }

        BaseExtension android = project.extensions.findByName("android")
        if (android) {
            Map<String, String> options = [
                    (APT_OPTION_MODULE_NAME): project.name
            ]

            android.defaultConfig.javaCompileOptions.annotationProcessorOptions.arguments(options)

            if (project.plugins.hasPlugin(AppPlugin)) {
                android.registerTransform(new RouterTransform(project))
            }
        }
    }
}