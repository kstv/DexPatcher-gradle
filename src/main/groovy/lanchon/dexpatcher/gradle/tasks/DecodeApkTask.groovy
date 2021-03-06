package lanchon.dexpatcher.gradle.tasks

import groovy.transform.CompileStatic
import lanchon.dexpatcher.gradle.Resolver
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

/*
usage: apktool [-q|--quiet OR -v|--verbose] d[ecode] [options] <file_apk>
    --api <API>                    The numeric api-level of the file to generate, e.g. 14 for ICS.
 -b,--no-debug-info                don't write out debug info (.local, .param, .line, etc.)
 -d,--debug                        Decode in debug mode. Check project page for more info.
    --debug-line-prefix <prefix>   Smali line prefix when decoding in debug mode. Default is "a=0;// ".
 -f,--force                        Force delete destination directory.
 -k,--keep-broken-res              Use if there was an error and some resources were dropped, e.g.
                                   "Invalid config flags detected. Dropping resources", but you
                                   want to decode them anyway, even with errors. You will have to
                                   fix them manually before building.
 -m,--match-original               Keeps files to closest to original as possible. Prevents rebuild.
 -o,--output <dir>                 The name of folder that gets written. Default is apk.out
 -p,--frame-path <dir>             Uses framework files located in <dir>.
 -r,--no-res                       Do not decode resources.
 -s,--no-src                       Do not decode sources.
 -t,--frame-tag <tag>              Uses framework files tagged by <tag>.
*/

@CompileStatic
class DecodeApkTask extends ApktoolBaseTask {

    def apkFile
    def outputDir
    def frameworkDir
    def frameworkTag
    def apiLevel
    @Input boolean decodeResources = true
    @Input boolean decodeClasses = true
    @Input boolean keepBrokenResources
    @Input boolean forceOverwrite

    @InputFile File getApkFile() { project.file(apkFile) }
    @OutputDirectory File getOutputDir() { project.file(outputDir) }
    @Optional @InputDirectory File getFrameworkDir() { Resolver.resolveNullableFile(project, frameworkDir) }
    @Optional @Input String getFrameworkTag() { Resolver.resolve(frameworkTag) as String }
    @Optional @Input Integer getApiLevel() { Resolver.resolve(apiLevel) as Integer }

    @Override List<String> getArgs() {
        ArrayList<String> args = new ArrayList()
        args.add('decode')
        args.addAll(['--output', getOutputDir() as String])
        if (getFrameworkDir()) args.addAll(['--frame-path', getFrameworkDir() as String])
        def theFrameworkTag = getFrameworkTag()
        if (theFrameworkTag) args.addAll(['--frame-tag', theFrameworkTag])
        def theApiLevel = getApiLevel()
        if (theApiLevel) args.addAll(['--api', theApiLevel as String])
        if (!decodeResources) args.add('--no-res')
        if (!decodeClasses) args.add('--no-src')
        if (keepBrokenResources) args.add('--keep-broken-res')
        if (forceOverwrite) args.add('--force')
        args.addAll(getExtraArgs())
        args.add(getApkFile() as String)
        return args;
    }

    @Override void afterExec() {
        if (!getOutputDir().isDirectory()) throw new RuntimeException('No output generated')
    }

}
