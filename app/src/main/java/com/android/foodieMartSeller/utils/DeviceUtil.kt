package com.fitnesstraker.core.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.opengl.GLES20
import android.os.Build.*
import android.os.Build.VERSION.*
import android.os.Environment
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.android.foodieMartSeller.network.pojos.device.App
import com.android.foodieMartSeller.network.pojos.device.DeviceInfoPost
import io.reactivex.Single
import java.io.File

object DeviceUtil {

    private fun getIncremental() = INCREMENTAL

    private fun getCodename() = CODENAME

    private fun getSDKInt() = SDK_INT

    private fun getRelease() = RELEASE

    private fun getBoard() = BOARD

    private fun getBootloader() = BOOTLOADER

    private fun getBrand() = BRAND

    private fun getDevice() = DEVICE

    private fun getDisplay() = DISPLAY

    private fun getFingerprint() = FINGERPRINT

    private fun getHardware() = HARDWARE

    private fun getHost() = HOST

    private fun getId() = ID

    private fun getManufacturer() = MANUFACTURER

    private fun getModel() = MODEL

    private fun getProduct() = PRODUCT

    private fun getSupported32BitAbis() = SUPPORTED_32_BIT_ABIS.toList()

    private fun getSupported64BitAbis() = SUPPORTED_64_BIT_ABIS.toList()

    private fun getSupportedAbis() = SUPPORTED_ABIS.toList()

    private fun getTags() = TAGS

    private fun getType() = TYPE

    private fun isPhysicalDevice() = !isEmulator()

    private fun getAndroidId() = Settings.Secure.ANDROID_ID

   /* fun getDeviceToken(onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token.toString()
                    onSuccess.invoke(token)
                }
            }.addOnFailureListener {
                onError.invoke(it)
            }
    }*/

    private fun getSecurityPatch(): String? {
        return if (isGreaterThanMarshmallow()) {
            SECURITY_PATCH
        } else {
            null
        }
    }

    private fun getPreviewSdkint(): Int? {
        return if (isGreaterThanMarshmallow()) {
            PREVIEW_SDK_INT
        } else {
            null
        }
    }

    private fun getBaseOS(): String? {
        return if (isGreaterThanMarshmallow()) {
            BASE_OS
        } else {
            null
        }
    }

    private fun isGreaterThanMarshmallow(): Boolean {
        return SDK_INT >= VERSION_CODES.M
    }

    private fun isGreaterThanOreo(): Boolean {
        return SDK_INT >= VERSION_CODES.O
    }


    private fun getSystemFeatures(context: Context): List<String> {
        val list = ArrayList<String>()
        val sensorManager = (context.getSystemService(Service.SENSOR_SERVICE)) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            list.add(PackageManager.FEATURE_SENSOR_PROXIMITY)
        }

        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        val isSDSupportedDevice: Boolean = Environment.isExternalStorageRemovable()

        if (isSDSupportedDevice && isSDPresent) {
            list.add("android.software.adoptable_storage")
        }

        return list
    }

    private fun isEmulator(): Boolean {
        var rating = -1
        var newRating = 0
        if (rating < 0) {
            if (PRODUCT.contains("sdk") ||
                PRODUCT.contains("Andy") ||
                PRODUCT.contains("ttVM_Hdragon") ||
                PRODUCT.contains("google_sdk") ||
                PRODUCT.contains("Droid4X") ||
                PRODUCT.contains("nox") ||
                PRODUCT.contains("sdk_x86") ||
                PRODUCT.contains("sdk_google") ||
                PRODUCT.contains("vbox86p")
            ) {
                newRating++
            }
            if (MANUFACTURER == "unknown" ||
                MANUFACTURER == "Genymotion" ||
                MANUFACTURER.contains("Andy") ||
                MANUFACTURER.contains("MIT") ||
                MANUFACTURER.contains("nox") ||
                MANUFACTURER.contains("TiantianVM")
            ) {
                newRating++
            }
            if (BRAND == "generic" ||
                BRAND == "generic_x86" ||
                BRAND == "TTVM" ||
                BRAND.contains("Andy")
            ) {
                newRating++
            }
            if (DEVICE.contains("generic") ||
                DEVICE.contains("generic_x86") ||
                DEVICE.contains("Andy") ||
                DEVICE.contains("ttVM_Hdragon") ||
                DEVICE.contains("Droid4X") ||
                DEVICE.contains("nox") ||
                DEVICE.contains("generic_x86_64") ||
                DEVICE.contains("vbox86p")
            ) {
                newRating++
            }
            if (MODEL == "sdk" ||
                MODEL == "google_sdk" ||
                MODEL.contains("Droid4X") ||
                MODEL.contains("TiantianVM") ||
                MODEL.contains("Andy") ||
                MODEL == "Android SDK built for x86_64" ||
                MODEL == "Android SDK built for x86"
            ) {
                newRating++
            }
            if (HARDWARE == "goldfish" ||
                HARDWARE == "vbox86" ||
                HARDWARE.contains("nox") ||
                HARDWARE.contains("ttVM_x86")
            ) {
                newRating++
            }
            if (FINGERPRINT.contains("generic/sdk/generic") ||
                FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                FINGERPRINT.contains("Andy") ||
                FINGERPRINT.contains("ttVM_Hdragon") ||
                FINGERPRINT.contains("generic_x86_64") ||
                FINGERPRINT.contains("generic/google_sdk/generic") ||
                FINGERPRINT.contains("vbox86p") ||
                FINGERPRINT.contains("generic/vbox86p/vbox86p")
            ) {
                newRating++
            }
            try {
                val opengl = GLES20.glGetString(GLES20.GL_RENDERER)
                if (opengl != null) {
                    if (opengl.contains("Bluestacks") ||
                        opengl.contains("Translator")
                    ) newRating += 10
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val sharedFolder = File(
                    (Environment
                        .getExternalStorageDirectory().toString() +
                            File.separatorChar
                            ) + "windows" +
                            File.separatorChar
                                .toString() + "BstSharedFolder"
                )
                if (sharedFolder.exists()) {
                    newRating += 10
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            rating = newRating
        }
        return rating > THRESHOLD_FOR_EMULATOR_CHECK
    }

    fun getDeviceAllInfo(context: Context, deviceToken: String): DeviceInfoPost {
        return DeviceInfoPost(
            device = getDevice(),
            type = getType(),
            id = getId(),
            androidId = getAndroidId(),
            baseOS = getBaseOS(),
            board = getBoard(),
            bootloader = getBootloader(),
            brand = getBrand(),
            codename = getCodename(),
            deviceToken = deviceToken,
            display = getDisplay(),
            fingerprint = getFingerprint(),
            hardware = getHardware(),
            host = getHost(),
            incremental = getIncremental(),
            isPhysicalDevice = isPhysicalDevice(),
            manufacturer = getManufacturer(),
            model = getModel(),
            previewSdkint = getPreviewSdkint(),
            product = getProduct(),
            release = getRelease(),
            sdkint = getSDKInt().toString(),
            securityPatch = getSecurityPatch(),
            supported32BitAbis = getSupported32BitAbis(),
            supported64BitAbis = getSupported64BitAbis(),
            supportedAbis = getSupportedAbis(),
            systemFeatures = getSystemFeatures(context),
            tags = getTags()
        )
    }

    fun getMusicAppsList(activity: FragmentActivity?): Single<List<App>> {
        return Single.create {
            val listOfApps = mutableListOf<App>()
            val packageManager = activity?.application?.packageManager
            val apps = packageManager?.getInstalledApplications(PackageManager.GET_META_DATA)

            apps?.forEach { applicationInfo ->
                if (isGreaterThanOreo() && applicationInfo.category == ApplicationInfo.CATEGORY_AUDIO) {
                    listOfApps.add(
                        App(
                            icon = applicationInfo.loadIcon(packageManager),
                            name = applicationInfo.loadLabel(packageManager).toString(),
                            packageName = applicationInfo.packageName
                        )
                    )
                }
            }
            it.onSuccess(listOfApps)
        }
    }

    fun goToAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    private const val THRESHOLD_FOR_EMULATOR_CHECK = 3
}
