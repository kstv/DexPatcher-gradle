package lanchon.dexpatcher.gradle

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.api.LibraryVariantOutput
import groovy.transform.CompileStatic
import lanchon.dexpatcher.gradle.extensions.AbstractPatcherExtension
import lanchon.dexpatcher.gradle.extensions.PatchLibraryExtension
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.plugins.ExtensionAware;

@CompileStatic
class PatchLibraryPlugin extends AbstractPatcherPlugin {

    protected PatchLibraryExtension patchLibraryExtension
    protected LibraryExtension libraryExtension
    protected DefaultDomainObjectSet<LibraryVariant> libraryVariants

    @Override protected AbstractPatcherExtension getPatcherExtension() { patchLibraryExtension }
    @Override protected BaseExtension getAndroidExtension() { libraryExtension }
    @Override protected DomainObjectSet<? extends BaseVariant> getAndroidVariants() { libraryVariants }

    @Override
    void apply(Project project) {

        super.apply(project)

        def subextensions = (dexpatcherConfig as ExtensionAware).extensions
        patchLibraryExtension = (PatchLibraryExtension) subextensions.create(PatchLibraryExtension.EXTENSION_NAME, PatchLibraryExtension)

        project.plugins.apply(LibraryPlugin)
        libraryExtension = project.extensions.getByType(LibraryExtension)
        libraryVariants = libraryExtension.libraryVariants

        applyAndroid()
    }

    @Override
    protected void applyAndroid() {

        super.applyAndroid()

        def libClasspath = project.dependencies.create(dexpatcherConfig.getLibClasspath())
        project.configurations.getByName('provided').dependencies.add(libClasspath)

        project.afterEvaluate {
            libraryVariants.all { LibraryVariant variant ->
                processJavaResources(variant)
            }
        }

    }

    // Java Resources

    private void processJavaResources(LibraryVariant variant) {
        variant.outputs.each {
            if (it instanceof LibraryVariantOutput) {
                // TODO: Support bundling of java resources.
            }
        }
    }

}
