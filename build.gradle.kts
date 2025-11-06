// The buildscript block has been removed.

// You can apply plugins common to all subprojects here,
// but it's often cleaner to apply them only where needed (e.g., in app/build.gradle.kts).
// For this fix, leaving this file empty or with just alias declarations is fine.

plugins {
    // It's good practice to declare the root plugins here and set `apply false`
    // so subprojects can apply them without specifying a version.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
}
