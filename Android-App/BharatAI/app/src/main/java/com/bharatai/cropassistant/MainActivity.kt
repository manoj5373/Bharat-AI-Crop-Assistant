package com.bharatai.cropassistant

import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import com.bharatai.cropassistant.ui.theme.BharatAITheme
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import java.io.File
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

import org.pytorch.executorch.extension.llm.LlmCallback

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import android.location.Geocoder
import android.os.Build
import java.util.Locale
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

private const val ENABLE_LLAMA_EXPLANATION = true

// Application colours
private val Green = Color(0xFF166534)
private val LightGreen = Color(0xFFE8F5E9)
private val Background = Color(0xFFF8FAF7)
private val DarkText = Color(0xFF17251B)
private val GreyText = Color(0xFF647067)

// All text displayed in the application is kept in one place.
// Add future screen translations to this data class and translationsFor().
data class AppStrings(
    val appSubtitle: String,
    val welcome: String,
    val welcomeDescription: String,
    val scanTitle: String,
    val scanSubtitle: String,
    val scanButton: String,
    val servicesTitle: String,
    val diseaseTitle: String,
    val diseaseSubtitle: String,
    val pestTitle: String,
    val pestSubtitle: String,
    val assistantTitle: String,
    val assistantSubtitle: String,
    val historyTitle: String,
    val noScans: String,
    val historySubtitle: String,
    val home: String,
    val scan: String,
    val assistant: String,
    val history: String,
    val profile: String,
    val scanComingSoon: String,
    val diseaseSelected: String,
    val pestSelected: String,
    val assistantSelected: String,
    val scanSelected: String,
    val historySelected: String,
    val profileSelected: String,
    val backHome: String,
    val scannerTitle: String,
    val scannerDescription: String,
    val openCamera: String,
    val uploadImage: String,
    val retakeImage: String,
    val changeImage: String,
    val analyzeCrop: String,
    val selectImageFirst: String,

// Diagnosis Results translations
    val diagnosisResultsTitle: String,
    val sampleDiagnosis: String,
    val diseaseLabel: String,
    val leafSpot: String,
    val severityLabel: String,
    val moderate: String,
    val confidenceLabel: String,
    val sampleValueOnly: String,
    val sampleDisclaimer: String,
    val backToScanner: String,

    // Treatment & Intervention translations
    val treatmentTitle: String,
    val treatmentButton: String,
    val treatmentDescription: String,
    val preventionTitle: String,
    val backToResults: String,

    val managementTitle: String,
    val managementText: String,
    val treatmentPreventionText: String,
    val treatmentCaution: String,
    val healthyResultTitle: String,
    val healthyResultText: String,

    // Farmer Profile translations
    val profileTitle: String,
    val profileDescription: String,
    val personalInformation: String,
    val farmerNameLabel: String,
    val phoneNumberLabel: String,
    val farmInformation: String,
    val villageLabel: String,
    val districtLabel: String,
    val talukLabel: String,
    val stateLabel: String,
    val editProfile: String,
    val saveProfile: String,
    val phoneNumberError: String,

    // Regional Advisory translations
    val regionalAdvisoryTitle: String,
    val regionalAdvisoryDescription: String,
    val advisoryNavLabel: String,
    val currentLocation: String,
    val selectLocation: String,
    val selectDistrict: String,
    val selectTaluk: String,
    val selectVillage: String,
    val confirmLocation: String,
    val confirmedLocation: String,
    val farmingAdvisories: String,
    val refresh: String,
    val all: String,
    val diseaseManagement: String,
    val symptoms: String,
    val recommendation: String,
    val source: String,
    val reference: String,
    val noAdvisories: String,
    val noCategoryAdvisories: String,

    // Regional Advisory status messages
    val locationNotDetected: String,
    val detectingLocation: String,
    val locationPermissionDenied: String,
    val locationPermissionRequired: String,
    val unableToDetectLocation: String,
    val addressNotFound: String,
    val noDistrictData: String,
    val noTalukData: String,
    val noVillageData: String,
    val regionalEmptyDescription: String,


    // Diagnosis Results - Severity and Safety
    val diseaseDetectionTitle: String = "Disease Detection",
    val severityAssessmentTitle: String = "Severity Assessment",
    val severityLevelLabel: String = "Severity Level:",
    val healthySeverity: String = "Healthy",
    val mildSeverity: String = "Mild",
    val moderateSeverity: String = "Moderate",
    val severeSeverity: String = "Severe",
    val modelScoreLabel: String = "Model score:",
    val classScoresTitle: String = "Class scores",
    val safetyNoticeTitle: String = "Safety Notice",
    val safetyNoticeText: String =
        "Do not apply pesticides based only on AI image classification. Consult a local sericulture or agriculture expert.",
    val analyzingImageMessage: String = "Analyzing leaf image...",
    val analysisFailedLabel: String = "Analysis failed:",
    val assessingSeverityMessage: String = "Assessing leaf severity...",
    val severityUnavailableMessage: String =
        "Severity assessment is not available.",
    val llamaExplanationTitle: String = "Bharat AI Explanation",
    val llamaPreparingMessage: String = "Preparing Llama model...",
    val llamaGeneratingMessage: String =
        "Generating farmer-friendly explanation...",
    val llamaGeneratedMessage: String = "Explanation generated!",
    val diseaseFreeLeaves: String,
    val leafRust: String
)

private fun translationsFor(language: String): AppStrings {
    return when (language) {
        "Hindi" -> AppStrings(
            appSubtitle = "ऑफ़लाइन AI-संचालित कृषि सहायक",
            welcome = "नमस्ते किसान!",
            welcomeDescription = "आपकी फसल, हमारी जिम्मेदारी",
            scanTitle = "फसल स्कैन करें",
            scanSubtitle = "फसल की सेहत जांचने के लिए उसकी तस्वीर लें या अपलोड करें।",
            scanButton = "📷  अभी स्कैन करें",
            servicesTitle = "फसल स्वास्थ्य सेवाएं",
            diseaseTitle = "रोग पहचान",
            diseaseSubtitle = "फसल रोगों की पहचान करें",
            pestTitle = "कीट पहचान",
            pestSubtitle = "हानिकारक कीटों की पहचान करें",
            assistantTitle = "AI कृषि सहायक",
            assistantSubtitle = "स्मार्ट कृषि मार्गदर्शन प्राप्त करें",
            historyTitle = "हाल की गतिविधियां",
            noScans = "अभी तक कोई स्कैन नहीं",
            historySubtitle = "आपकी फसल जांच का इतिहास यहां दिखाई देगा।",
            home = "होम",
            scan = "स्कैन",
            assistant = "सहायक",
            history = "इतिहास",
            profile = "प्रोफ़ाइल",
            scanComingSoon = "फसल स्कैनिंग सुविधा अगले विकास चरण में उपलब्ध होगी।",
            diseaseSelected = "रोग पहचान चुना गया।",
            pestSelected = "कीट पहचान चुना गया।",
            assistantSelected = "AI कृषि सहायक चुना गया।",
            scanSelected = "फसल स्कैन चुना गया।",
            historySelected = "फसल जांच का इतिहास चुना गया।",
            profileSelected = "प्रोफ़ाइल चुनी गई।",
            backHome = "होम पर वापस जाएं",
            scannerTitle = "फसल स्कैनर",
            scannerDescription = "फसल का स्वास्थ्य जांचने के लिए उसकी तस्वीर लें या अपलोड करें।",
            openCamera = "कैमरा खोलें",
            uploadImage = "तस्वीर अपलोड करें",
            retakeImage = "दोबारा तस्वीर लें",
            changeImage = "तस्वीर बदलें",
            analyzeCrop = "फसल का विश्लेषण करें",
            selectImageFirst = "कृपया पहले फसल की तस्वीर चुनें।",

            diagnosisResultsTitle = "निदान परिणाम",
            sampleDiagnosis = "नमूना निदान",
            diseaseLabel = "रोग:",
            leafSpot = "पत्ती का धब्बा रोग",
            severityLabel = "गंभीरता:",
            moderate = "मध्यम",
            confidenceLabel = "विश्वास स्तर:",
            sampleValueOnly = "केवल नमूना मान",
            sampleDisclaimer = "यह केवल प्रदर्शन के लिए नमूना डेटा है, वास्तविक AI निदान नहीं। प्रशिक्षित AI मॉडल के एकीकरण के बाद रोग की पहचान उपलब्ध होगी।",
            backToScanner = "स्कैनर पर वापस जाएं",

            treatmentTitle = "उपचार और हस्तक्षेप",
            treatmentButton = "उपचार और हस्तक्षेप देखें",
            treatmentDescription = "रोग के अनुसार उपचार और फसल प्रबंधन मार्गदर्शन।",
            preventionTitle = "रोकथाम के उपाय",
            backToResults = "परिणामों पर वापस जाएं",

            managementTitle = "प्रबंधन के उपाय",
            managementText = "पहचाने गए रोग के अनुसार उचित प्रबंधन अपनाएं।",
            treatmentPreventionText = "रोकथाम के उपाय",
            treatmentCaution = "रेशम के कीड़ों को खिलाने के लिए उपयोग की जाने वाली पत्तियों पर कोई भी रसायन इस्तेमाल करने से पहले स्थानीय रेशम विशेषज्ञ से सलाह लें।",
            healthyResultTitle = "पत्ती स्वस्थ दिखाई देती है",
            healthyResultText = "फसल की नियमित निगरानी करें और उचित स्वच्छता बनाए रखें।",

            profileTitle = "किसान प्रोफ़ाइल",
            profileDescription = "अपनी व्यक्तिगत और कृषि जानकारी प्रबंधित करें",
            personalInformation = "व्यक्तिगत जानकारी",
            farmerNameLabel = "किसान का नाम",
            phoneNumberLabel = "फ़ोन नंबर",
            farmInformation = "खेत की जानकारी",
            villageLabel = "गाँव",
            districtLabel = "ज़िला",
            stateLabel = "राज्य",
            editProfile = "प्रोफ़ाइल संपादित करें",
            saveProfile = "प्रोफ़ाइल सहेजें",
            phoneNumberError = "फ़ोन नंबर में ठीक 10 अंक होने चाहिए।",

            // Regional Advisory
            regionalAdvisoryTitle = "क्षेत्रीय कृषि सलाह",
            regionalAdvisoryDescription = "अपने क्षेत्र के अनुसार कृषि मार्गदर्शन प्राप्त करें।",
            currentLocation = "मेरी वर्तमान लोकेशन का उपयोग करें",
            selectLocation = "स्थान स्वयं चुनें",
            selectDistrict = "ज़िला चुनें",
            selectTaluk = "तालुक चुनें",
            selectVillage = "गाँव चुनें",
            confirmLocation = "स्थान की पुष्टि करें",
            confirmedLocation = "पुष्ट स्थान",
            farmingAdvisories = "क्षेत्रीय कृषि सलाह",
            refresh = "रीफ्रेश करें",
            all = "सभी",
            diseaseManagement = "रोग प्रबंधन",
            symptoms = "लक्षण",
            recommendation = "सिफारिश",
            source = "स्रोत",
            reference = "संदर्भ",
            noAdvisories = "अभी कोई सलाह उपलब्ध नहीं है",
            advisoryNavLabel = "सलाह",
            noCategoryAdvisories = "इस श्रेणी में कोई सलाह उपलब्ध नहीं है",
            talukLabel = "तालुक",

            locationNotDetected = "अभी स्थान का पता नहीं चला है",
            detectingLocation = "आपका स्थान पता लगाया जा रहा है...",
            locationPermissionDenied = "स्थान की अनुमति नहीं दी गई।",
            locationPermissionRequired = "स्थान की अनुमति आवश्यक है।",
            unableToDetectLocation = "स्थान का पता नहीं चला। कृपया पुनः प्रयास करें।",
            addressNotFound = "निर्देशांक मिले, लेकिन पता नहीं मिला।",
            noDistrictData = "के लिए ज़िले का डेटा उपलब्ध नहीं है",
            noTalukData = "के लिए तालुक का डेटा उपलब्ध नहीं है",
            noVillageData = "के लिए गाँव का डेटा उपलब्ध नहीं है",
            regionalEmptyDescription = "आपके चुने हुए क्षेत्र के लिए सत्यापित कृषि सलाह यहाँ दिखाई देगी।",


            diseaseDetectionTitle = "रोग पहचान",
            severityAssessmentTitle = "गंभीरता का आकलन",
            severityLevelLabel = "गंभीरता स्तर:",
            healthySeverity = "स्वस्थ",
            mildSeverity = "हल्का",
            moderateSeverity = "मध्यम",
            severeSeverity = "गंभीर",
            modelScoreLabel = "मॉडल स्कोर:",
            classScoresTitle = "श्रेणी स्कोर",
            safetyNoticeTitle = "सुरक्षा सूचना",
            safetyNoticeText = "केवल AI छवि वर्गीकरण के आधार पर कीटनाशकों का उपयोग न करें। स्थानीय रेशम उत्पादन या कृषि विशेषज्ञ से सलाह लें।",
            analyzingImageMessage = "पत्ती की तस्वीर का विश्लेषण किया जा रहा है...",
            analysisFailedLabel = "विश्लेषण विफल:",
            assessingSeverityMessage = "पत्ती की गंभीरता का आकलन किया जा रहा है...",
            severityUnavailableMessage = "गंभीरता का आकलन उपलब्ध नहीं है।",
            llamaExplanationTitle = "KrishiMitra AI स्पष्टीकरण",
            llamaPreparingMessage = "Llama मॉडल तैयार किया जा रहा है...",
            llamaGeneratingMessage = "किसान के लिए सरल स्पष्टीकरण तैयार किया जा रहा है...",
            diseaseFreeLeaves = "रोग-मुक्त पत्तियाँ",
            leafRust = "पत्ती का रतुआ रोग",
            llamaGeneratedMessage = "स्पष्टीकरण तैयार हो गया!"

        )

        "Kannada" -> AppStrings(
            appSubtitle = "ಆಫ್‌ಲೈನ್ AI ಆಧಾರಿತ ಕೃಷಿ ಸಹಾಯಕ",
            welcome = "ನಮಸ್ಕಾರ ರೈತರೇ!",
            welcomeDescription = "ನಿಮ್ಮ ಬೆಳೆ, ನಮ್ಮ ಜವಾಬ್ದಾರಿ",
            scanTitle = "ಬೆಳೆಯನ್ನು ಸ್ಕ್ಯಾನ್ ಮಾಡಿ",
            scanSubtitle = "ಬೆಳೆಯ ಆರೋಗ್ಯವನ್ನು ಪರಿಶೀಲಿಸಲು ಅದರ ಚಿತ್ರವನ್ನು ತೆಗೆದುಕೊಳ್ಳಿ ಅಥವಾ ಅಪ್‌ಲೋಡ್ ಮಾಡಿ.",
            scanButton = "📷  ಈಗ ಸ್ಕ್ಯಾನ್ ಮಾಡಿ",
            servicesTitle = "ಬೆಳೆ ಆರೋಗ್ಯ ಸೇವೆಗಳು",
            diseaseTitle = "ರೋಗ ಪತ್ತೆ",
            diseaseSubtitle = "ಬೆಳೆ ರೋಗಗಳನ್ನು ಗುರುತಿಸಿ",
            pestTitle = "ಕೀಟ ಪತ್ತೆ",
            pestSubtitle = "ಹಾನಿಕಾರಕ ಕೀಟಗಳನ್ನು ಗುರುತಿಸಿ",
            assistantTitle = "AI ಕೃಷಿ ಸಹಾಯಕ",
            assistantSubtitle = "ಸ್ಮಾರ್ಟ್ ಕೃಷಿ ಮಾರ್ಗದರ್ಶನ ಪಡೆಯಿರಿ",
            historyTitle = "ಇತ್ತೀಚಿನ ಚಟುವಟಿಕೆಗಳು",
            noScans = "ಇನ್ನೂ ಯಾವುದೇ ಸ್ಕ್ಯಾನ್ ಇಲ್ಲ",
            historySubtitle = "ನಿಮ್ಮ ಬೆಳೆ ರೋಗನಿರ್ಣಯದ ಇತಿಹಾಸ ಇಲ್ಲಿ ಕಾಣಿಸುತ್ತದೆ.",
            home = "ಮುಖಪುಟ",
            scan = "ಸ್ಕ್ಯಾನ್",
            assistant = "ಸಹಾಯಕ",
            history = "ಇತಿಹಾಸ",
            profile = "ಪ್ರೊಫೈಲ್",
            scanComingSoon = "ಬೆಳೆ ಸ್ಕ್ಯಾನಿಂಗ್ ಸೌಲಭ್ಯ ಮುಂದಿನ ಅಭಿವೃದ್ಧಿ ಹಂತದಲ್ಲಿ ಲಭ್ಯವಿರುತ್ತದೆ.",
            diseaseSelected = "ರೋಗ ಪತ್ತೆ ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ.",
            pestSelected = "ಕೀಟ ಪತ್ತೆ ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ.",
            assistantSelected = "AI ಕೃಷಿ ಸಹಾಯಕವನ್ನು ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ.",
            scanSelected = "ಬೆಳೆ ಸ್ಕ್ಯಾನ್ ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ.",
            historySelected = "ಬೆಳೆ ರೋಗನಿರ್ಣಯದ ಇತಿಹಾಸವನ್ನು ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ.",
            profileSelected = "ಪ್ರೊಫೈಲ್ ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ.",
            backHome = "ಮುಖಪುಟಕ್ಕೆ ಹಿಂತಿರುಗಿ",
            scannerTitle = "ಬೆಳೆ ಸ್ಕ್ಯಾನರ್",
            scannerDescription = "ಬೆಳೆಯ ಆರೋಗ್ಯವನ್ನು ಪರಿಶೀಲಿಸಲು ಅದರ ಚಿತ್ರವನ್ನು ತೆಗೆದುಕೊಳ್ಳಿ ಅಥವಾ ಅಪ್‌ಲೋಡ್ ಮಾಡಿ.",
            openCamera = "ಕ್ಯಾಮೆರಾ ತೆರೆಯಿರಿ",
            uploadImage = "ಚಿತ್ರವನ್ನು ಅಪ್‌ಲೋಡ್ ಮಾಡಿ",
            retakeImage = "ಮತ್ತೆ ಚಿತ್ರ ತೆಗೆಯಿರಿ",
            changeImage = "ಚಿತ್ರ ಬದಲಾಯಿಸಿ",analyzeCrop = "ಬೆಳೆಯನ್ನು ವಿಶ್ಲೇಷಿಸಿ",
            selectImageFirst = "ದಯವಿಟ್ಟು ಮೊದಲು ಬೆಳೆಯ ಚಿತ್ರವನ್ನು ಆಯ್ಕೆಮಾಡಿ.",

            diagnosisResultsTitle = "ರೋಗನಿರ್ಣಯದ ಫಲಿತಾಂಶಗಳು",
            sampleDiagnosis = "ಮಾದರಿ ರೋಗನಿರ್ಣಯ",
            diseaseLabel = "ರೋಗ:",
            leafSpot = "ಎಲೆ ಚುಕ್ಕೆ ರೋಗ",
            severityLabel = "ತೀವ್ರತೆ:",
            moderate = "ಮಧ್ಯಮ",
            confidenceLabel = "ವಿಶ್ವಾಸದ ಮಟ್ಟ:",
            sampleValueOnly = "ಮಾದರಿ ಮೌಲ್ಯ ಮಾತ್ರ",
            sampleDisclaimer = "ಇದು ಪ್ರದರ್ಶನಕ್ಕಾಗಿ ಮಾತ್ರ ನೀಡಲಾದ ಮಾದರಿ ಮಾಹಿತಿ, ನಿಜವಾದ AI ರೋಗನಿರ್ಣಯವಲ್ಲ. ತರಬೇತಿ ಪಡೆದ AI ಮಾದರಿಯನ್ನು ಸಂಯೋಜಿಸಿದ ನಂತರ ರೋಗ ಪತ್ತೆ ಲಭ್ಯವಾಗುತ್ತದೆ.",
            backToScanner = "ಸ್ಕ್ಯಾನರ್‌ಗೆ ಹಿಂತಿರುಗಿ",

            treatmentTitle = "ಚಿಕಿತ್ಸೆ ಮತ್ತು ನಿರ್ವಹಣೆ",
            treatmentButton = "ಚಿಕಿತ್ಸೆ ಮತ್ತು ನಿರ್ವಹಣೆ ನೋಡಿ",
            treatmentDescription = "ರೋಗಕ್ಕೆ ಅನುಗುಣವಾದ ಚಿಕಿತ್ಸೆ ಮತ್ತು ಬೆಳೆ ನಿರ್ವಹಣೆಯ ಮಾರ್ಗದರ್ಶನ.",
            preventionTitle = "ತಡೆಗಟ್ಟುವ ಕ್ರಮಗಳು",
            backToResults = "ಫಲಿತಾಂಶಗಳಿಗೆ ಹಿಂತಿರುಗಿ",

            managementTitle = "ನಿರ್ವಹಣಾ ಕ್ರಮಗಳು",
            managementText = "ಗುರುತಿಸಲಾದ ರೋಗಕ್ಕೆ ಅನುಗುಣವಾಗಿ ಸೂಕ್ತ ನಿರ್ವಹಣಾ ಕ್ರಮಗಳನ್ನು ಅನುಸರಿಸಿ.",
            treatmentPreventionText = "ತಡೆಗಟ್ಟುವ ಕ್ರಮಗಳು",
            treatmentCaution = "ರೇಷ್ಮೆ ಹುಳುಗಳಿಗೆ ಆಹಾರವಾಗಿ ಬಳಸುವ ಎಲೆಗಳ ಮೇಲೆ ಯಾವುದೇ ರಾಸಾಯನಿಕ ಬಳಸುವ ಮೊದಲು ಸ್ಥಳೀಯ ರೇಷ್ಮೆ ತಜ್ಞರನ್ನು ಸಂಪರ್ಕಿಸಿ.",
            healthyResultTitle = "ಎಲೆ ಆರೋಗ್ಯಕರವಾಗಿ ಕಾಣುತ್ತದೆ",
            healthyResultText = "ಬೆಳೆಯನ್ನು ನಿಯಮಿತವಾಗಿ ಪರಿಶೀಲಿಸಿ ಮತ್ತು ಸೂಕ್ತ ಸ್ವಚ್ಛತೆಯನ್ನು ಕಾಪಾಡಿಕೊಳ್ಳಿ.",

            profileTitle = "ರೈತರ ಪ್ರೊಫೈಲ್",
            profileDescription = "ನಿಮ್ಮ ವೈಯಕ್ತಿಕ ಮತ್ತು ಕೃಷಿ ಮಾಹಿತಿಯನ್ನು ನಿರ್ವಹಿಸಿ",
            personalInformation = "ವೈಯಕ್ತಿಕ ಮಾಹಿತಿ",
            farmerNameLabel = "ರೈತರ ಹೆಸರು",
            phoneNumberLabel = "ದೂರವಾಣಿ ಸಂಖ್ಯೆ",
            farmInformation = "ಕೃಷಿ ಮಾಹಿತಿ",
            villageLabel = "ಗ್ರಾಮ",
            districtLabel = "ಜಿಲ್ಲೆ",
            stateLabel = "ರಾಜ್ಯ",
            editProfile = "ಪ್ರೊಫೈಲ್ ಸಂಪಾದಿಸಿ",
            saveProfile = "ಪ್ರೊಫೈಲ್ ಉಳಿಸಿ",
            phoneNumberError = "ದೂರವಾಣಿ ಸಂಖ್ಯೆಯಲ್ಲಿ ನಿಖರವಾಗಿ 10 ಅಂಕಿಗಳಿರಬೇಕು.",

            // Regional Advisory
            regionalAdvisoryTitle = "ಪ್ರಾದೇಶಿಕ ಕೃಷಿ ಸಲಹೆ",
            regionalAdvisoryDescription = "ನಿಮ್ಮ ಪ್ರದೇಶಕ್ಕೆ ಅನುಗುಣವಾಗಿ ಕೃಷಿ ಮಾರ್ಗದರ್ಶನ ಪಡೆಯಿರಿ.",
            currentLocation = "ನನ್ನ ಪ್ರಸ್ತುತ ಸ್ಥಳವನ್ನು ಬಳಸಿ",
            selectLocation = "ಸ್ಥಳವನ್ನು ಕೈಯಾರೆ ಆಯ್ಕೆಮಾಡಿ",
            selectDistrict = "ಜಿಲ್ಲೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ",
            selectTaluk = "ತಾಲೂಕನ್ನು ಆಯ್ಕೆಮಾಡಿ",
            selectVillage = "ಗ್ರಾಮವನ್ನು ಆಯ್ಕೆಮಾಡಿ",
            confirmLocation = "ಸ್ಥಳವನ್ನು ದೃಢೀಕರಿಸಿ",
            confirmedLocation = "ದೃಢೀಕರಿಸಿದ ಸ್ಥಳ",
            farmingAdvisories = "ಪ್ರಾದೇಶಿಕ ಕೃಷಿ ಸಲಹೆಗಳು",
            refresh = "ಮತ್ತೆ ಲೋಡ್ ಮಾಡಿ",
            all = "ಎಲ್ಲಾ",
            diseaseManagement = "ರೋಗ ನಿರ್ವಹಣೆ",
            symptoms = "ಲಕ್ಷಣಗಳು",
            recommendation = "ಶಿಫಾರಸು",
            advisoryNavLabel = "ಸಲಹೆ",
            source = "ಮೂಲ",
            reference = "ಉಲ್ಲೇಖ",
            noAdvisories = "ಇನ್ನೂ ಯಾವುದೇ ಸಲಹೆಗಳು ಲಭ್ಯವಿಲ್ಲ",
            noCategoryAdvisories = "ಈ ವರ್ಗದಲ್ಲಿ ಯಾವುದೇ ಸಲಹೆಗಳಿಲ್ಲ",
            talukLabel = "ತಾಲೂಕು",

            locationNotDetected = "ಸ್ಥಳ ಇನ್ನೂ ಪತ್ತೆಯಾಗಿಲ್ಲ",
            detectingLocation = "ನಿಮ್ಮ ಸ್ಥಳವನ್ನು ಪತ್ತೆಹಚ್ಚಲಾಗುತ್ತಿದೆ...",
            locationPermissionDenied = "ಸ್ಥಳದ ಅನುಮತಿಯನ್ನು ನಿರಾಕರಿಸಲಾಗಿದೆ.",
            locationPermissionRequired = "ಸ್ಥಳದ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ.",
            unableToDetectLocation = "ಸ್ಥಳವನ್ನು ಪತ್ತೆಹಚ್ಚಲು ಸಾಧ್ಯವಾಗಲಿಲ್ಲ. ದಯವಿಟ್ಟು ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ.",
            addressNotFound = "ನಿರ್ದೇಶಾಂಕಗಳು ಪತ್ತೆಯಾಗಿವೆ, ಆದರೆ ವಿಳಾಸ ಸಿಗಲಿಲ್ಲ.",
            noDistrictData = "ಇದಕ್ಕೆ ಜಿಲ್ಲೆ ಮಾಹಿತಿ ಲಭ್ಯವಿಲ್ಲ",
            noTalukData = "ಇದಕ್ಕೆ ತಾಲೂಕು ಮಾಹಿತಿ ಲಭ್ಯವಿಲ್ಲ",
            noVillageData = "ಇದಕ್ಕೆ ಗ್ರಾಮ ಮಾಹಿತಿ ಲಭ್ಯವಿಲ್ಲ",
            regionalEmptyDescription = "ನೀವು ಆಯ್ಕೆ ಮಾಡಿದ ಪ್ರದೇಶದ ಪರಿಶೀಲಿತ ಕೃಷಿ ಸಲಹೆಗಳು ಇಲ್ಲಿ ಕಾಣಿಸುತ್ತವೆ.",


            diseaseDetectionTitle = "ರೋಗ ಪತ್ತೆ",
            severityAssessmentTitle = "ತೀವ್ರತೆಯ ಮೌಲ್ಯಮಾಪನ",
            severityLevelLabel = "ತೀವ್ರತೆಯ ಮಟ್ಟ:",
            healthySeverity = "ಆರೋಗ್ಯಕರ",
            mildSeverity = "ಸೌಮ್ಯ",
            moderateSeverity = "ಮಧ್ಯಮ",
            severeSeverity = "ತೀವ್ರ",
            modelScoreLabel = "ಮಾದರಿ ಅಂಕ:",
            classScoresTitle = "ವರ್ಗಗಳ ಅಂಕಗಳು",
            safetyNoticeTitle = "ಸುರಕ್ಷತಾ ಸೂಚನೆ",
            safetyNoticeText = "AI ಚಿತ್ರ ವರ್ಗೀಕರಣದ ಆಧಾರದ ಮೇಲೆ ಮಾತ್ರ ಕೀಟನಾಶಕಗಳನ್ನು ಬಳಸಬೇಡಿ. ಸ್ಥಳೀಯ ರೇಷ್ಮೆ ಅಥವಾ ಕೃಷಿ ತಜ್ಞರನ್ನು ಸಂಪರ್ಕಿಸಿ.",
            analyzingImageMessage = "ಎಲೆಯ ಚಿತ್ರವನ್ನು ವಿಶ್ಲೇಷಿಸಲಾಗುತ್ತಿದೆ...",
            analysisFailedLabel = "ವಿಶ್ಲೇಷಣೆ ವಿಫಲವಾಗಿದೆ:",
            assessingSeverityMessage = "ಎಲೆಯ ತೀವ್ರತೆಯನ್ನು ಮೌಲ್ಯಮಾಪನ ಮಾಡಲಾಗುತ್ತಿದೆ...",
            severityUnavailableMessage = "ತೀವ್ರತೆಯ ಮೌಲ್ಯಮಾಪನ ಲಭ್ಯವಿಲ್ಲ.",
            llamaExplanationTitle = "KrishiMitra AI ವಿವರಣೆ",
            llamaPreparingMessage = "Llama ಮಾದರಿಯನ್ನು ಸಿದ್ಧಪಡಿಸಲಾಗುತ್ತಿದೆ...",
            llamaGeneratingMessage = "ರೈತರಿಗೆ ಸರಳ ವಿವರಣೆಯನ್ನು ರಚಿಸಲಾಗುತ್ತಿದೆ...",
            diseaseFreeLeaves = "ರೋಗರಹಿತ ಎಲೆಗಳು",
            leafRust = "ಎಲೆ ತುಕ್ಕು ರೋಗ",
            llamaGeneratedMessage = "ವಿವರಣೆ ಸಿದ್ಧವಾಗಿದೆ!"

        )

        "Telugu" -> AppStrings(
            appSubtitle = "ఆఫ్‌లైన్ AI ఆధారిత వ్యవసాయ సహాయకుడు",
            welcome = "నమస్కారం రైతు!",
            welcomeDescription = "మీ పంట, మా బాధ్యత",
            scanTitle = "పంటను స్కాన్ చేయండి",
            scanSubtitle = "పంట ఆరోగ్యాన్ని తనిఖీ చేయడానికి చిత్రాన్ని తీయండి లేదా అప్‌లోడ్ చేయండి.",
            scanButton = "📷  ఇప్పుడే స్కాన్ చేయండి",
            servicesTitle = "పంట ఆరోగ్య సేవలు",
            diseaseTitle = "వ్యాధి గుర్తింపు",
            diseaseSubtitle = "పంట వ్యాధులను గుర్తించండి",
            pestTitle = "కీటకాల గుర్తింపు",
            pestSubtitle = "హానికరమైన పురుగులను గుర్తించండి",
            assistantTitle = "AI వ్యవసాయ సహాయకుడు",
            assistantSubtitle = "స్మార్ట్ వ్యవసాయ మార్గదర్శకత్వం పొందండి",
            historyTitle = "ఇటీవలి కార్యకలాపాలు",
            noScans = "ఇంకా స్కాన్లు లేవు",
            historySubtitle = "మీ పంట నిర్ధారణ చరిత్ర ఇక్కడ కనిపిస్తుంది.",
            home = "హోమ్",
            scan = "స్కాన్",
            assistant = "సహాయకుడు",
            history = "చరిత్ర",
            profile = "ప్రొఫైల్",
            scanComingSoon = "పంట స్కానింగ్ సదుపాయం తదుపరి అభివృద్ధి దశలో అందుబాటులో ఉంటుంది.",
            diseaseSelected = "వ్యాధి గుర్తింపు ఎంచుకోబడింది.",
            pestSelected = "కీటకాల గుర్తింపు ఎంచుకోబడింది.",
            assistantSelected = "AI వ్యవసాయ సహాయకుడు ఎంచుకోబడింది.",
            scanSelected = "పంట స్కాన్ ఎంచుకోబడింది.",
            historySelected = "పంట నిర్ధారణ చరిత్ర ఎంచుకోబడింది.",
            profileSelected = "ప్రొఫైల్ ఎంచుకోబడింది.",
            backHome = "హోమ్‌కు తిరిగి వెళ్లండి",
            scannerTitle = "పంట స్కానర్",
            scannerDescription = "పంట ఆరోగ్యాన్ని తనిఖీ చేయడానికి చిత్రాన్ని తీయండి లేదా అప్‌లోడ్ చేయండి.",
            openCamera = "కెమెరాను తెరవండి",
            uploadImage = "చిత్రాన్ని అప్‌లోడ్ చేయండి",
            retakeImage = "మళ్లీ ఫోటో తీయండి",
            changeImage = "చిత్రాన్ని మార్చండి",
            analyzeCrop = "పంటను విశ్లేషించండి",
            selectImageFirst = "దయచేసి ముందుగా పంట చిత్రాన్ని ఎంచుకోండి.",

            diagnosisResultsTitle = "వ్యాధి నిర్ధారణ ఫలితాలు",
            sampleDiagnosis = "నమూనా వ్యాధి నిర్ధారణ",
            diseaseLabel = "వ్యాధి:",
            leafSpot = "ఆకు మచ్చ వ్యాధి",
            severityLabel = "తీవ్రత:",
            moderate = "మధ్యస్థం",
            confidenceLabel = "నమ్మక స్థాయి:",
            sampleValueOnly = "నమూనా విలువ మాత్రమే",
            sampleDisclaimer = "ఇది ప్రదర్శన కోసం మాత్రమే ఇచ్చిన నమూనా సమాచారం, నిజమైన AI నిర్ధారణ కాదు. శిక్షణ పొందిన AI మోడల్‌ను అనుసంధానించిన తర్వాత వ్యాధి గుర్తింపు అందుబాటులో ఉంటుంది.",
            backToScanner = "స్కానర్‌కు తిరిగి వెళ్లండి",

            treatmentTitle = "చికిత్స మరియు నిర్వహణ",
            treatmentButton = "చికిత్స మరియు నిర్వహణ చూడండి",
            treatmentDescription = "వ్యాధికి అనుగుణమైన చికిత్స మరియు పంట నిర్వహణ మార్గదర్శకత్వం.",
            preventionTitle = "నివారణ చర్యలు",
            backToResults = "ఫలితాలకు తిరిగి వెళ్లండి",

            managementTitle = "నిర్వహణ చర్యలు",
            managementText = "గుర్తించిన వ్యాధికి అనుగుణంగా తగిన నిర్వహణ చర్యలను అనుసరించండి.",
            treatmentPreventionText = "నివారణ చర్యలు",
            treatmentCaution = "పట్టుపురుగులకు ఆహారంగా ఉపయోగించే ఆకులపై ఏదైనా రసాయనం వాడే ముందు స్థానిక సెరికల్చర్ నిపుణుడిని సంప్రదించండి.",
            healthyResultTitle = "ఆకు ఆరోగ్యంగా కనిపిస్తోంది",
            healthyResultText = "పంటను క్రమం తప్పకుండా పరిశీలించండి మరియు సరైన పరిశుభ్రతను పాటించండి.",
            profileTitle = "రైతు ప్రొఫైల్",
            profileDescription = "మీ వ్యక్తిగత మరియు వ్యవసాయ సమాచారాన్ని నిర్వహించండి",
            personalInformation = "వ్యక్తిగత సమాచారం",
            farmerNameLabel = "రైతు పేరు",
            phoneNumberLabel = "ఫోన్ నంబర్",
            farmInformation = "వ్యవసాయ సమాచారం",
            advisoryNavLabel = "సలహా",
            villageLabel = "గ్రామం",
            districtLabel = "జిల్లా",
            stateLabel = "రాష్ట్రం",
            editProfile = "ప్రొఫైల్‌ను సవరించండి",
            saveProfile = "ప్రొఫైల్‌ను సేవ్ చేయండి",
            phoneNumberError = "ఫోన్ నంబర్‌లో ఖచ్చితంగా 10 అంకెలు ఉండాలి.",

            // Regional Advisory
            regionalAdvisoryTitle = "ప్రాంతీయ వ్యవసాయ సలహా",
            regionalAdvisoryDescription = "మీ ప్రాంతానికి అనుగుణంగా వ్యవసాయ మార్గదర్శకత్వం పొందండి.",
            currentLocation = "నా ప్రస్తుత స్థానాన్ని ఉపయోగించండి",
            selectLocation = "స్థానాన్ని మాన్యువల్‌గా ఎంచుకోండి",
            selectDistrict = "జిల్లాను ఎంచుకోండి",
            selectTaluk = "తాలూకాను ఎంచుకోండి",
            selectVillage = "గ్రామాన్ని ఎంచుకోండి",
            confirmLocation = "స్థానాన్ని నిర్ధారించండి",
            confirmedLocation = "నిర్ధారించిన స్థానం",
            farmingAdvisories = "ప్రాంతీయ వ్యవసాయ సలహాలు",
            refresh = "మళ్లీ లోడ్ చేయండి",
            all = "అన్నీ",
            diseaseManagement = "వ్యాధి నిర్వహణ",
            symptoms = "లక్షణాలు",
            recommendation = "సిఫార్సు",
            source = "మూలం",
            reference = "సూచన",
            noAdvisories = "ప్రస్తుతం ఎలాంటి సలహాలు అందుబాటులో లేవు",
            noCategoryAdvisories = "ఈ వర్గంలో ఎలాంటి సలహాలు లేవు",
            talukLabel = "తాలూకా",

            locationNotDetected = "స్థానం ఇంకా గుర్తించబడలేదు",
            detectingLocation = "మీ స్థానాన్ని గుర్తిస్తోంది...",
            locationPermissionDenied = "స్థాన అనుమతి నిరాకరించబడింది.",
            locationPermissionRequired = "స్థాన అనుమతి అవసరం.",
            unableToDetectLocation = "స్థానాన్ని గుర్తించలేకపోయాము. దయచేసి మళ్లీ ప్రయత్నించండి.",
            addressNotFound = "కోఆర్డినేట్లు గుర్తించబడ్డాయి, కానీ చిరునామా కనుగొనబడలేదు.",
            noDistrictData = "దీనికి జిల్లా సమాచారం అందుబాటులో లేదు",
            noTalukData = "దీనికి తాలూకా సమాచారం అందుబాటులో లేదు",
            noVillageData = "దీనికి గ్రామ సమాచారం అందుబాటులో లేదు",
            regionalEmptyDescription = "మీరు ఎంచుకున్న ప్రాంతానికి సంబంధించిన ధృవీకరించిన వ్యవసాయ సలహాలు ఇక్కడ కనిపిస్తాయి.",


            diseaseDetectionTitle = "వ్యాధి గుర్తింపు",
            severityAssessmentTitle = "తీవ్రత అంచనా",
            severityLevelLabel = "తీవ్రత స్థాయి:",
            healthySeverity = "ఆరోగ్యకరమైనది",
            mildSeverity = "స్వల్పం",
            moderateSeverity = "మధ్యస్థం",
            severeSeverity = "తీవ్రమైనది",
            modelScoreLabel = "మోడల్ స్కోర్:",
            classScoresTitle = "తరగతుల స్కోర్లు",
            safetyNoticeTitle = "భద్రతా సూచన",
            safetyNoticeText = "AI చిత్ర వర్గీకరణ ఆధారంగా మాత్రమే పురుగుమందులను ఉపయోగించవద్దు. స్థానిక సెరికల్చర్ లేదా వ్యవసాయ నిపుణుడిని సంప్రదించండి.",
            analyzingImageMessage = "ఆకు చిత్రాన్ని విశ్లేషిస్తోంది...",
            analysisFailedLabel = "విశ్లేషణ విఫలమైంది:",
            assessingSeverityMessage = "ఆకు తీవ్రతను అంచనా వేస్తోంది...",
            severityUnavailableMessage = "తీవ్రత అంచనా అందుబాటులో లేదు.",
            llamaExplanationTitle = "KrishiMitra AI వివరణ",
            llamaPreparingMessage = "Llama మోడల్‌ను సిద్ధం చేస్తోంది...",
            llamaGeneratingMessage = "రైతులకు సరళమైన వివరణను రూపొందిస్తోంది...",
            diseaseFreeLeaves = "వ్యాధి లేని ఆకులు",
            leafRust = "ఆకు తుప్పు వ్యాధి",
            llamaGeneratedMessage = "వివరణ సిద్ధమైంది!"

        )

        else -> AppStrings(
            appSubtitle = "An Offline AI-Powered Farming Assistant",
            welcome = "Namaste, Farmer!",
            welcomeDescription = "Your crops, our responsibility",
            scanTitle = "Scan Your Crop",
            scanSubtitle = "Capture or upload a crop image to check its health.",
            scanButton = "📷  Scan Now",
            servicesTitle = "Crop Health Services",
            diseaseTitle = "Disease Detection",
            diseaseSubtitle = "Identify crop diseases",
            pestTitle = "Pest Detection",
            pestSubtitle = "Identify harmful pests",
            assistantTitle = "AI Farming Assistant",
            assistantSubtitle = "Get smart agricultural guidance",
            historyTitle = "Recent Activity",
            noScans = "No previous scans",
            historySubtitle = "Your crop diagnosis history will appear here.",
            home = "Home",
            scan = "Scan",
            assistant = "Assistant",
            history = "History",
            profile = "Profile",
            scanComingSoon = "Crop scanning will be available in the next development stage.",
            diseaseSelected = "Disease detection selected.",
            pestSelected = "Pest detection selected.",
            assistantSelected = "AI Farming Assistant selected.",
            scanSelected = "Crop scanning selected.",
            historySelected = "Diagnosis history selected.",
            profileSelected = "Profile selected.",
            backHome = "Back to Home",
            scannerTitle = "Crop Scanner",
            scannerDescription = "Capture or upload a crop image to check its health.",
            openCamera = "Open Camera",
            uploadImage = "Upload Image",
            retakeImage = "Retake",
            changeImage = "Change Image",
            analyzeCrop = "Analyze Crop",
            selectImageFirst = "Please select a crop image first.",

            diagnosisResultsTitle = "Diagnosis Results",
            sampleDiagnosis = "Sample Diagnosis",
            diseaseLabel = "Disease:",
            leafSpot = "Leaf Spot",
            severityLabel = "Severity:",
            moderate = "Moderate",
            confidenceLabel = "Confidence:",
            sampleValueOnly = "Sample value only",
            sampleDisclaimer = "This is sample data for demonstration only, not an actual AI diagnosis. Disease detection will become available after integration of the trained AI model.",
            backToScanner = "Back to Scanner",

            treatmentTitle = "Treatment & Intervention",
            treatmentButton = "View Treatment & Intervention",
            treatmentDescription = "Treatment and crop management guidance based on the detected disease.",
            preventionTitle = "Prevention Measures",
            backToResults = "Back to Results",

            managementTitle = "Management Measures",
            managementText = "Follow appropriate management practices for the identified disease.",
            treatmentPreventionText = "Prevention Measures",
            treatmentCaution = "Consult a local sericulture expert before applying any chemical to leaves used as silkworm feed.",
            healthyResultTitle = "The leaf appears healthy",
            healthyResultText = "Monitor the crop regularly and maintain proper field hygiene.",
            profileTitle = "Farmer Profile",
            profileDescription = "Manage your personal and farm information",
            personalInformation = "Personal Information",
            farmerNameLabel = "Farmer Name",
            phoneNumberLabel = "Phone Number",
            farmInformation = "Farm Information",
            villageLabel = "Village",
            districtLabel = "District",
            stateLabel = "State",
            editProfile = "Edit Profile",
            saveProfile = "Save Profile",
            phoneNumberError = "Phone number must contain exactly 10 digits.",
            // Regional Advisory
            regionalAdvisoryTitle = "Regional Advisory",
            regionalAdvisoryDescription = "Get farming guidance based on your region.",
            currentLocation = "Use My Current Location",
            selectLocation = "Select Location Manually",
            selectDistrict = "Select District",
            selectTaluk = "Select Taluk",
            selectVillage = "Select Village",
            confirmLocation = "Confirm Location",
            confirmedLocation = "Confirmed Location",
            farmingAdvisories = "Regional Farming Advisories",
            refresh = "Refresh",
            all = "All",
            diseaseManagement = "Disease Management",
            symptoms = "Symptoms",
            recommendation = "Recommendation",
            advisoryNavLabel = "Advisory",
            source = "Source",
            reference = "Reference",
            noAdvisories = "No advisories available yet",
            noCategoryAdvisories = "No advisories in this category",
            talukLabel = "Taluk",

            locationNotDetected = "Location not detected yet",
            detectingLocation = "Detecting your location...",
            locationPermissionDenied = "Location permission denied.",
            locationPermissionRequired = "Location permission is required.",
            unableToDetectLocation = "Unable to detect location. Please try again.",
            addressNotFound = "Coordinates detected, but address not found.",
            noDistrictData = "No district data available for",
            noTalukData = "No taluk data available for",
            noVillageData = "No village data available for",
            regionalEmptyDescription = "Verified farming recommendations for your selected region will appear here.",


            diseaseDetectionTitle = "Disease Detection",
            severityAssessmentTitle = "Severity Assessment",
            severityLevelLabel = "Severity Level:",
            healthySeverity = "Healthy",
            mildSeverity = "Mild",
            moderateSeverity = "Moderate",
            severeSeverity = "Severe",
            modelScoreLabel = "Model score:",
            classScoresTitle = "Class scores",
            safetyNoticeTitle = "Safety Notice",
            safetyNoticeText = "Do not apply pesticides based only on AI image classification. Consult a local sericulture or agriculture expert.",
            analyzingImageMessage = "Analyzing leaf image...",
            analysisFailedLabel = "Analysis failed:",
            assessingSeverityMessage = "Assessing leaf severity...",
            severityUnavailableMessage = "Severity assessment is not available.",
            llamaExplanationTitle = "KrishiMitra AI Explanation",
            llamaPreparingMessage = "Preparing Llama model...",
            llamaGeneratingMessage = "Generating farmer-friendly explanation...",
            diseaseFreeLeaves = "Disease-Free Leaves",
            leafRust = "Leaf Rust",
            llamaGeneratedMessage = "Explanation generated!"
        )
    }
}

// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BharatAITheme {
                BharatAIHomeScreen()
            }
        }

    }
}

// Home Screen
@Composable
fun BharatAIHomeScreen() {

    var selectedLanguage by remember { mutableStateOf("English") }
    var languageMenu by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Home") }
    var message by remember { mutableStateOf("") }

// Farmer Profile Information
    var farmerName by remember { mutableStateOf("") }
    var farmerPhone by remember { mutableStateOf("") }
    var farmerVillage by remember { mutableStateOf("") }
    var farmerDistrict by remember { mutableStateOf("") }
    var farmerState by remember { mutableStateOf("") }

// Selected Crop Image
    var selectedCropImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

// Context
    val context = LocalContext.current

// DataStore connection
    val profileDataStore = remember {
        ProfileDataStore(context)
    }

    val savedProfile by profileDataStore.profile.collectAsState(
        initial = FarmerProfile()
    )

    val scope = rememberCoroutineScope()

// Load saved farmer information
    LaunchedEffect(savedProfile) {
        farmerName = savedProfile.name
        farmerPhone = savedProfile.phone
        farmerVillage = savedProfile.village
        farmerDistrict = savedProfile.district
        farmerState = savedProfile.state
    }

// Existing classifier variables
    var classifier by remember {
        mutableStateOf<MulberryClassifier?>(null)
    }

    var prediction by remember {
        mutableStateOf<MulberryPrediction?>(null)
    }

    // Severity classifier variables
    var severityClassifier by remember {
        mutableStateOf<MulberrySeverityClassifier?>(null)
    }

    var severityPrediction by remember {
        mutableStateOf<SeverityPrediction?>(null)
    }

    var severityError by remember {
        mutableStateOf<String?>(null)
    }

    var isAnalyzing by remember {
        mutableStateOf(false)
    }

    var analysisError by remember {
        mutableStateOf<String?>(null)
    }

    val strings = translationsFor(selectedLanguage)

    if (selectedTab == "Results") {
        selectedCropImage?.let { image ->
            DiagnosisResultsScreen(
                image = image,
                strings = strings,
                selectedLanguage = selectedLanguage,
                prediction = prediction,
                severityPrediction = severityPrediction,
                severityError = severityError,
                isAnalyzing = isAnalyzing,
                analysisError = analysisError,
                onLanguageChange = { language ->
                    selectedLanguage = language
                },
                onTreatmentClick = {
                    selectedTab = "Treatment"
                },
                onBack = {
                    selectedTab = "Scan"
                }
            )
        }
        return
    }

    if (selectedTab == "Treatment") {
        TreatmentScreen(
            prediction = prediction,
            strings = strings,
            selectedLanguage = selectedLanguage,
            onBack = {
                selectedTab = "Results"
            }
        )
        return
    }

    if (selectedTab == "Advisory") {
        RegionalAdvisoryScreen(
            strings = strings,
            languageCode = when (selectedLanguage) {
                "Hindi" -> "hi"
                "Kannada" -> "kn"
                "Telugu" -> "te"
                else -> "en"
            },
            onBack = { selectedTab = "Home" }
        )
        return
    }

    if (selectedTab == "Profile") {
        ProfileScreen(
            farmerName = farmerName,
            farmerPhone = farmerPhone,
            farmerVillage = farmerVillage,
            farmerDistrict = farmerDistrict,
            farmerState = farmerState,
            strings = strings,

            onFarmerNameChange = { farmerName = it },
            onFarmerPhoneChange = { farmerPhone = it },
            onFarmerVillageChange = { farmerVillage = it },
            onFarmerDistrictChange = { farmerDistrict = it },
            onFarmerStateChange = { farmerState = it },

            onBack = { selectedTab = "Home" },

            onSave = {
                scope.launch {
                    profileDataStore.saveProfile(
                        FarmerProfile(
                            name = farmerName,
                            phone = farmerPhone,
                            village = farmerVillage,
                            district = farmerDistrict,
                            state = farmerState
                        )
                    )
                }
            },
        )
        return
    }

    if (selectedTab == "Scan") {
        CropScannerScreen(
            strings = strings,
            selectedImage = selectedCropImage,
            isAnalyzing = isAnalyzing,

            onImageChange = { image ->
                selectedCropImage = image
            },

            onBack = {
                selectedCropImage = null
                selectedTab = "Home"
            },


            onAnalyze = { image ->

                if (isAnalyzing) {
                    Log.w(
                        "BharatAI_Debug",
                        "Analysis already running. Ignoring duplicate request."
                    )
                } else {

                    Log.d("BharatAI_Debug", "Starting crop analysis")

                    selectedCropImage = image
                    isAnalyzing = true

                    // Reset previous results
                    analysisError = null
                    severityError = null
                    prediction = null
                    severityPrediction = null

                    // Keep existing loaded classifiers
                    val existingClassifier = classifier
                    val existingSeverityClassifier = severityClassifier

                    scope.launch {
                        try {

                            // --------------------------------
                            // 1. Existing disease classification
                            // --------------------------------

                            Log.d("BharatAI_Debug", "Starting disease classifier")

                            val (loadedClassifier, diseaseResult) =
                                withContext(Dispatchers.Default) {

                                    val activeClassifier =
                                        existingClassifier ?: MulberryClassifier(context)

                                    val result = activeClassifier.predict(image)

                                    activeClassifier to result
                                }

                            classifier = loadedClassifier
                            prediction = diseaseResult

                            Log.d(
                                "BharatAI_Debug",
                                "Disease prediction: ${diseaseResult.diseaseName}"
                            )

                            // --------------------------------
                            // 2. New severity classification
                            // --------------------------------

                            try {

                                Log.d("SEVERITY_TEST", "Starting severity analysis")

                                val (loadedSeverityClassifier, severityResult) =
                                    withContext(Dispatchers.Default) {

                                        val activeSeverityClassifier =
                                            existingSeverityClassifier
                                                ?: MulberrySeverityClassifier(context)

                                        val severityInput =
                                            MulberrySeverityPreprocessor.preprocess(image)

                                        val result =
                                            activeSeverityClassifier.predictPreprocessed(
                                                severityInput
                                            )

                                        activeSeverityClassifier to result
                                    }

                                severityClassifier = loadedSeverityClassifier
                                severityPrediction = severityResult
                                severityError = null

                                Log.d(
                                    "SEVERITY_TEST",
                                    "Severity prediction: ${severityResult.severityName}"
                                )

                            } catch (severityException: Exception) {

                                Log.e(
                                    "SEVERITY_TEST",
                                    "Severity analysis failed",
                                    severityException
                                )

                                severityPrediction = null

                                severityError =
                                    severityException.localizedMessage
                                        ?: "Unable to assess severity."
                            }

                            // --------------------------------
                            // 3. Navigate to results
                            // --------------------------------

                            selectedTab = "Results"

                        } catch (e: Exception) {

                            // Disease classification failure
                            Log.e(
                                "BharatAI_Debug",
                                "Disease classification failed",
                                e
                            )

                            analysisError =
                                e.localizedMessage
                                    ?: "Unable to analyze this image."

                            selectedTab = "Results"

                        } finally {

                            isAnalyzing = false

                            Log.d(
                                "BharatAI_Debug",
                                "Analysis finished"
                            )
                        }
                    }
                }
            }

        )
        return
    }

    val languageDisplayName = when (selectedLanguage) {
        "Hindi" -> "हिन्दी"
        "Kannada" -> "ಕನ್ನಡ"
        "Telugu" -> "తెలుగు"
        else -> "English"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Green,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌱", fontSize = 25.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "KrishiMitra AI",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Green
                )

                Text(
                    text = strings.appSubtitle,
                    fontSize = 11.sp,
                    color = GreyText
                )
            }

            Box {
                OutlinedButton(
                    onClick = { languageMenu = true },
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
                ) {
                    Text("🌐 $languageDisplayName", fontSize = 12.sp)
                }

                DropdownMenu(
                    expanded = languageMenu,
                    onDismissRequest = { languageMenu = false }
                ) {
                    listOf("English", "Hindi", "Kannada", "Telugu").forEach { language ->
                        val displayName = when (language) {
                            "Hindi" -> "हिन्दी"
                            "Kannada" -> "ಕನ್ನಡ"
                            "Telugu" -> "తెలుగు"
                            else -> "English"
                        }

                        DropdownMenuItem(
                            text = { Text(displayName) },
                            onClick = {
                                selectedLanguage = language
                                languageMenu = false
                                message = ""
                            }
                        )
                    }
                }
            }
        }

        // Scrollable Home Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome section
            Column {
                Text(
                    text = strings.welcome,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = strings.welcomeDescription,
                    fontSize = 14.sp,
                    color = GreyText
                )
            }

            // Main Crop Scanning Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Green)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("🌿", fontSize = 35.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = strings.scanTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = strings.scanSubtitle,
                        fontSize = 13.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Button(
                        onClick = {
                            Log.d("BharatAI_Debug", "Scan Now button clicked")
                            selectedTab = "Scan"
                            message = ""
                            Log.d("BharatAI_Debug", "Selected tab: $selectedTab")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Green
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = strings.scanButton,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Features heading
            Text(
                text = strings.servicesTitle,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            // Disease and Pest feature cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🍃",
                    title = strings.diseaseTitle,
                    subtitle = strings.diseaseSubtitle
                ) {
                    message = strings.diseaseSelected
                }

                FeatureCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🐛",
                    title = strings.pestTitle,
                    subtitle = strings.pestSubtitle
                ) {
                    message = strings.pestSelected
                }
            }

            // Regional Advisory Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTab = "Advisory"
                        message = ""
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌾", fontSize = 35.sp)

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = strings.regionalAdvisoryTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green
                        )

                        Text(
                            text = strings.regionalAdvisoryDescription,
                            fontSize = 12.sp,
                            color = GreyText
                        )
                    }
                }
            }

            // Recent Activity
            Text(
                text = strings.historyTitle,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "📋  ${strings.noScans}",
                        fontWeight = FontWeight.Medium,
                        color = DarkText
                    )

                    Text(
                        text = strings.historySubtitle,
                        fontSize = 12.sp,
                        color = GreyText
                    )
                }
            }

            // Interaction feedback
            if (message.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightGreen)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(14.dp),
                        color = Green,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Bottom Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavigationItem(
                emoji = "🏠",
                title = strings.home,
                selected = selectedTab == "Home"
            ) {
                selectedTab = "Home"
                message = ""
            }

            NavigationItem(
                emoji = "📷",
                title = strings.scan,
                selected = selectedTab == "Scan"
            ) {
                selectedTab = "Scan"
                message = ""
            }

            NavigationItem(
                emoji = "🌾",
                title = strings.advisoryNavLabel,
                selected = selectedTab == "Advisory"
            ) {
                selectedTab = "Advisory"
                message = ""
            }

            NavigationItem(
                emoji = "📋",
                title = strings.history,
                selected = selectedTab == "History"
            ) {
                selectedTab = "History"
                message = strings.historySelected
            }

            NavigationItem(
                emoji = "👤",
                title = strings.profile,
                selected = selectedTab == "Profile"
            ) {
                selectedTab = "Profile"
                message = strings.profileSelected
            }
        }
    }
}


@Composable
private fun DiagnosisResultsScreen(
    image: Bitmap,
    strings: AppStrings,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    prediction: MulberryPrediction?,
    severityPrediction: SeverityPrediction?,
    severityError: String?,
    isAnalyzing: Boolean,
    analysisError: String?,
    onTreatmentClick: () -> Unit,
    onBack: () -> Unit
){
    val appContext = androidx.compose.ui.platform.LocalContext.current

    val llamaAssistant = remember(appContext) {
        LlamaAssistant(appContext.applicationContext)
    }

    val scope = rememberCoroutineScope()

    val offlineTranslator = remember {
        OfflineTranslator()
    }

    var llamaLoaded by remember {
        mutableStateOf(false)
    }

    var isGeneratingExplanation by remember {
        mutableStateOf(false)
    }

    var llamaExplanation by remember {
        mutableStateOf("")
    }

    var translatedLlamaExplanation by remember {
        mutableStateOf("")
    }

    var translationJob by remember {
        mutableStateOf<Job?>(null)
    }

    var llamaStatus by remember {
        mutableStateOf("Ready to generate explanation")
    }

    val mainHandler = remember {
        android.os.Handler(android.os.Looper.getMainLooper())
    }

    val knowledgeBase = remember(appContext) {
        MulberryKnowledgeBase(appContext)
    }

    val diseaseInfo = remember(prediction, knowledgeBase) {
        prediction?.let {
            knowledgeBase.findDisease(it.diseaseName)
        }
    }


    LaunchedEffect(
        image,
        prediction?.diseaseName,
        prediction?.confidence,
        isAnalyzing,
        analysisError
    ) {

    val currentPrediction = prediction

        if (
            !ENABLE_LLAMA_EXPLANATION ||
            currentPrediction == null ||
            isAnalyzing ||
            analysisError != null
        ) {
            return@LaunchedEffect
        }

        val symptoms = diseaseInfo?.symptoms ?: emptyList()

        val safety = diseaseInfo?.safetyNote
            ?: "Consult a local sericulture or agriculture expert."


        val currentSeverity = severityPrediction

        val severityName = currentSeverity?.severityName
            ?: "Unavailable"

        val severityScore = currentSeverity?.let {
            String.format(
                java.util.Locale.US,
                "%.2f%%",
                it.confidence * 100
            )
        } ?: "Unavailable"

        val prompt = buildDiagnosisPrompt(
            disease = currentPrediction.diseaseName,
            severity = severityPrediction?.severityName ?: "Unavailable"
        )

        Log.d("LLAMA_DEBUG", "Selected language: $selectedLanguage")
        Log.d("LLAMA_DEBUG", "Prompt sent to Llama:\n$prompt")


        llamaExplanation = ""
        isGeneratingExplanation = true
        llamaStatus = 	strings.llamaPreparingMessage

        translatedLlamaExplanation = ""

        try {
            withContext(Dispatchers.IO) {
                if (!llamaLoaded) {
                    llamaAssistant.load()
                }
            }

            llamaLoaded = true
            llamaStatus = strings.llamaGeneratingMessage

            Log.d("LLAMA_DEBUG", "Before generate()")

            withContext(Dispatchers.IO) {
                llamaAssistant.generate(
                    prompt,
                    object : LlmCallback {

                        override fun onResult(result: String) {
                            Log.d("LLAMA_DEBUG", "Token received: $result")

                            mainHandler.post {
                                llamaExplanation += result
                            }
                        }

                        override fun onStats(stats: String) {
                            Log.d("LLAMA_DEBUG", "Generation completed: $stats")

                            mainHandler.post {
                                isGeneratingExplanation = false
                                llamaStatus = 	strings.llamaGeneratedMessage
                            }
                        }

                        override fun onError(errorCode: Int, message: String) {
                            Log.e(
                                "LLAMA_DEBUG",
                                "Generation error: $errorCode, $message"
                            )

                            mainHandler.post {
                                isGeneratingExplanation = false
                                llamaStatus = "Generation error $errorCode: $message"
                            }
                        }
                    }
                )
            }

            Log.d("LLAMA_DEBUG", "generate() returned")

        } catch (e: Exception) {
            mainHandler.post {
                isGeneratingExplanation = false
                llamaStatus = "Error: ${e.message}"
            }
        }
    }

    LaunchedEffect(
        llamaExplanation,
        selectedLanguage,
        isGeneratingExplanation
    ) {
        if (llamaExplanation.isBlank() || isGeneratingExplanation) {
            return@LaunchedEffect
        }

        if (selectedLanguage == "English") {
            translatedLlamaExplanation = llamaExplanation
            return@LaunchedEffect
        }

        Log.d(
            "TRANSLATION_DEBUG",
            "Selected language in UI: $selectedLanguage"
        )

        try {
            translatedLlamaExplanation = offlineTranslator.translate(
                text = llamaExplanation,
                targetLanguage = selectedLanguage
            )

            Log.d(
                "TRANSLATION_DEBUG",
                "Translation successful: $selectedLanguage"
            )

        } catch (e: Exception) {
            Log.e(
                "TRANSLATION_DEBUG",
                "Translation failed: ${e.message}"
            )

            translatedLlamaExplanation = llamaExplanation
        }
    }

    var languageMenu by remember { mutableStateOf(false) }

    val languageDisplayName = when (selectedLanguage) {
        "Hindi" -> "हिन्दी"
        "Kannada" -> "ಕನ್ನಡ"
        "Telugu" -> "తెలుగు"
        else -> "English"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // Back button and language selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "← ${strings.backToScanner}",
                    color = Green
                )
            }

            Box {
                OutlinedButton(
                    onClick = { languageMenu = true }
                ) {
                    Text("🌐 $languageDisplayName")
                }

                DropdownMenu(
                    expanded = languageMenu,
                    onDismissRequest = { languageMenu = false }
                ) {
                    listOf("English", "Hindi", "Kannada", "Telugu")
                        .forEach { language ->

                            val displayName = when (language) {
                                "Hindi" -> "हिन्दी"
                                "Kannada" -> "ಕನ್ನಡ"
                                "Telugu" -> "తెలుగు"
                                else -> "English"
                            }

                            DropdownMenuItem(
                                text = { Text(displayName) },
                                onClick = {
                                    onLanguageChange(language)
                                    languageMenu = false
                                }
                            )
                        }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Results heading
        Text(
            text = strings.diagnosisResultsTitle,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selected crop image
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "Selected crop image",
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sample diagnosis card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = strings.diagnosisResultsTitle,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Green
                )

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isAnalyzing -> {
                        CircularProgressIndicator(color = Green)
                        Text(strings.analyzingImageMessage)
                    }

                    analysisError != null -> {
                        Text(
                            text = 	"${strings.analysisFailedLabel} $analysisError",
                            color = Color.Red
                        )
                    }


                    prediction != null -> {
                        val translatedDiseaseName = when (
                            prediction.diseaseName.trim().lowercase()
                        ) {
                            "disease free leaves",
                            "disease-free leaves" -> strings.diseaseFreeLeaves

                            "leaf spot" -> strings.leafSpot

                            "leaf rust" -> strings.leafRust

                            else -> prediction.diseaseName
                        }

                        Text(
                            text = "${strings.diseaseLabel} $translatedDiseaseName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }


                    else -> {
                        Text("No prediction available.")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(12.dp))


// Severity Assessment
                Text(
                    text = strings.severityAssessmentTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Green
                )

                Spacer(modifier = Modifier.height(10.dp))

                when {
                    isAnalyzing -> {
                        Text(
                            text = strings.assessingSeverityMessage,
                            color = GreyText
                        )
                    }

                    severityPrediction != null -> {
                        val severity = severityPrediction

                        val severityDisplayName = when (severity.severityName.trim().lowercase()) {
                            "healthy" -> strings.healthySeverity
                            "mild" -> strings.mildSeverity
                            "moderate" -> strings.moderateSeverity
                            "severe" -> strings.severeSeverity
                            else -> severity.severityName
                        }

                        Text(
                            text = "${strings.severityLevelLabel} $severityDisplayName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${strings.modelScoreLabel} ${
                                "%.2f".format(
                                    java.util.Locale.US,
                                    severity.confidence * 100
                                )
                            }%",
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = strings.classScoresTitle,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText
                        )

                        val severityLabels = listOf(
                            strings.healthySeverity,
                            strings.mildSeverity,
                            strings.moderateSeverity,
                            strings.severeSeverity
                        )

                        severityLabels.forEachIndexed { index, label ->

                            val score = severity.scores[index]

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, color = DarkText)

                                    Text(
                                        text = "${
                                            "%.2f".format(
                                                java.util.Locale.US,
                                                score * 100
                                            )
                                        }%",
                                        color = DarkText
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { score.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(7.dp),
                                    color = Green,
                                    trackColor = LightGreen
                                )
                            }
                        }
                    }

                    severityError != null -> {
                        Text(
                            text = "Severity assessment unavailable: $severityError",
                            color = Color(0xFFB45309)
                        )
                    }

                    else -> {
                        Text(
                            text = strings.severityUnavailableMessage,
                            color = GreyText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = strings.llamaExplanationTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Green
                )

                Text(
                    text = when {
                        isGeneratingExplanation ->
                            strings.llamaGeneratingMessage

                        llamaExplanation.isNotBlank() ->
                            strings.llamaGeneratedMessage

                        else -> llamaStatus
                    },
                    color = GreyText,
                    fontSize = 13.sp
                )

                if (isGeneratingExplanation) {
                    CircularProgressIndicator(
                        color = Green,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (translatedLlamaExplanation.isNotBlank()) {
                    Text(
                        text = translatedLlamaExplanation,
                        color = DarkText,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Spacer(modifier = Modifier.height(20.dp))

// Safety Notice at the bottom
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF7ED)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = strings.safetyNoticeTitle,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = strings.safetyNoticeText,
                            color = Color(0xFFB45309),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(

                    onClick = onTreatmentClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = strings.treatmentButton,
                        color = Color.White
                    )
                }
            }
        }
    }
}


private fun buildDiagnosisPrompt(
    disease: String,
    severity: String
): String {

    return """
<|start_header_id|>system<|end_header_id|>
You explain mulberry leaf diagnosis results.
Use simple English.
Do not repeat the result or confidence.
Do not invent facts, causes or treatments.
Write exactly two short sentences.
Explain only what the detected result and severity mean.
<|eot_id|><|start_header_id|>user<|end_header_id|>
Detected result: $disease
Severity: $severity
<|eot_id|><|start_header_id|>assistant<|end_header_id|>
""".trimIndent()
}


@Composable
private fun TreatmentScreen(
    prediction: MulberryPrediction?,
    strings: AppStrings,
    selectedLanguage: String,
    onBack: () -> Unit
) {
    val diseaseName = prediction?.diseaseName ?: ""

    val advice = getTreatmentAdvice(
        diseaseName = diseaseName,
        language = selectedLanguage
    )

    val isHealthy = diseaseName.lowercase().let {
        it.contains("disease free") || it.contains("healthy")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(
                text = "← ${strings.backToResults}",
                color = Green
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.treatmentTitle,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {

                Text(
                    text = strings.diseaseLabel,
                    fontWeight = FontWeight.Bold,
                    color = Green
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = diseaseName.ifBlank {
                        "No diagnosis available"
                    },
                    fontSize = 18.sp,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isHealthy)
                        strings.healthyResultTitle
                    else
                        strings.managementTitle,
                    fontWeight = FontWeight.Bold,
                    color = Green,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isHealthy)
                        strings.healthyResultText
                    else
                        advice.first,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = strings.preventionTitle,
                    fontWeight = FontWeight.Bold,
                    color = Green,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = advice.second,
                    color = DarkText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "⚠ ${strings.treatmentCaution}",
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF8A4B08),
                fontSize = 14.sp
            )
        }
    }
}



// Reusable Feature Card
@Composable
fun FeatureCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(145.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = GreyText
            )
        }
    }
}

// Reusable Bottom Navigation Item
@Composable
fun NavigationItem(
    emoji: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 5.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 19.sp
        )

        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Green else GreyText
        )
    }
}
@Composable
private fun CropScannerScreen(
    strings: AppStrings,
    selectedImage: Bitmap?,
    isAnalyzing: Boolean,
    onImageChange: (Bitmap?) -> Unit,
    onBack: () -> Unit,
    onAnalyze: (Bitmap) -> Unit
) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                onImageChange(BitmapFactory.decodeStream(stream))
            }
        }
    }

    var cameraImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {
            cameraImageUri?.let { uri ->

                val bitmap = context.contentResolver
                    .openInputStream(uri)
                    ?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }

                if (bitmap != null) {

                    val orientation = context.contentResolver
                        .openInputStream(uri)
                        ?.use { stream ->
                            ExifInterface(stream).getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )
                        } ?: ExifInterface.ORIENTATION_NORMAL

                    val matrix = Matrix()

                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 ->
                            matrix.postRotate(90f)

                        ExifInterface.ORIENTATION_ROTATE_180 ->
                            matrix.postRotate(180f)

                        ExifInterface.ORIENTATION_ROTATE_270 ->
                            matrix.postRotate(270f)

                        ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
                            matrix.preScale(-1f, 1f)

                        ExifInterface.ORIENTATION_FLIP_VERTICAL ->
                            matrix.preScale(1f, -1f)

                        ExifInterface.ORIENTATION_TRANSPOSE -> {
                            matrix.preScale(-1f, 1f)
                            matrix.postRotate(270f)
                        }

                        ExifInterface.ORIENTATION_TRANSVERSE -> {
                            matrix.preScale(-1f, 1f)
                            matrix.postRotate(90f)
                        }
                    }

                    val correctedBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )

                    onImageChange(correctedBitmap)
                }
            }
        }
    }

    val launchCamera = {
        val imageDirectory = File(context.cacheDir, "crop_images")

        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs()
        }

        val imageFile = File.createTempFile(
            "crop_",
            ".jpg",
            imageDirectory
        )

        val imageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        cameraImageUri = imageUri

        cameraLauncher.launch(imageUri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← ${strings.backHome}", color = Green)
            }
        }

        selectedImage?.let { bitmap ->
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Selected crop image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }

        selectedImage?.let { bitmap ->
            Button(
                onClick = {
                    Log.d("BharatAI_Debug", "Analyze Crop button clicked")
                    Log.d("BharatAI_Debug", "Image size: ${bitmap.width} x ${bitmap.height}")
                    onAnalyze(bitmap)
                },
                enabled = !isAnalyzing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAnalyzing) {
                        "Analyzing..."
                    } else {
                        "🔍 ${strings.analyzeCrop}"
                    }
                )
            }
        }


        Text(
            text = "🌿",
            fontSize = 55.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.scannerTitle,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.scannerDescription,
            fontSize = 14.sp,
            color = GreyText
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = { launchCamera() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green
            )
        ) {
            Text("📷  ${strings.openCamera}")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                galleryLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🖼️  ${strings.uploadImage}")
        }
    }
}



@Composable
fun ProfileScreen(
    strings: AppStrings,
    farmerName: String,
    farmerPhone: String,
    farmerVillage: String,
    farmerDistrict: String,
    farmerState: String,

    onFarmerNameChange: (String) -> Unit,
    onFarmerPhoneChange: (String) -> Unit,
    onFarmerVillageChange: (String) -> Unit,
    onFarmerDistrictChange: (String) -> Unit,
    onFarmerStateChange: (String) -> Unit,
    onSave: () -> Unit,

    onBack: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // Header
        Text(
            text = strings.profileTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = Green
        )

        Text(
            text = strings.profileDescription,
            color = GreyText,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = strings.personalInformation,
                    style = MaterialTheme.typography.titleMedium,
                    color = Green
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = farmerName,
                    onValueChange = onFarmerNameChange,
                    label = { Text(strings.farmerNameLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = isEditing,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        disabledLabelColor = Color.DarkGray,
                        cursorColor = Green
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = farmerPhone,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 10) {
                            onFarmerPhoneChange(input)
                        }
                    },
                    label = { Text(strings.phoneNumberLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = isEditing,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        disabledLabelColor = Color.DarkGray,
                        cursorColor = Green
                    )
                )
                if (isEditing && farmerPhone.isNotEmpty() && farmerPhone.length != 10) {
                    Text(
                        text = "Phone number must contain exactly 10 digits",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Farm Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = strings.farmInformation,
                    style = MaterialTheme.typography.titleMedium,
                    color = Green
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = farmerVillage,
                    onValueChange = onFarmerVillageChange,
                    label = { Text(strings.villageLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = isEditing,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        disabledLabelColor = Color.DarkGray,
                        cursorColor = Green
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = farmerDistrict,
                    onValueChange = onFarmerDistrictChange,
                    label = { Text(strings.districtLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = isEditing,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        disabledLabelColor = Color.DarkGray,
                        cursorColor = Green
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = farmerState,
                    onValueChange = onFarmerStateChange,
                    label = { Text(strings.stateLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = isEditing,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        disabledLabelColor = Color.DarkGray,
                        cursorColor = Green
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Edit and Save Buttons
        if (isEditing) {
            Button(
                onClick = {
                    if (farmerPhone.length == 10) {
                        onSave()
                        isEditing = false
                    }
                },
                enabled = farmerPhone.length == 10,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                )
            ) {
                Text(strings.saveProfile)
            }
        } else {
            Button(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                )
            ) {
                Text(strings.editProfile)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings.backHome)
        }
    }
}


private fun getTreatmentAdvice(
    diseaseName: String,
    language: String
): Pair<String, String> {

    val disease = diseaseName.lowercase()

    val isRust = disease.contains("rust")
    val isSpot = disease.contains("spot")
    val isHealthy = disease.contains("disease free") ||
            disease.contains("healthy")

    return when (language) {

        "Hindi" -> when {
            isRust -> Pair(
                "पौधों के बीच पर्याप्त दूरी रखें ताकि हवा का संचार अच्छा रहे। पत्तियों की कटाई में अनावश्यक देरी न करें। प्रभावित पत्तियों की नियमित जांच करें।",
                "खेत में हवा का अच्छा प्रवाह बनाए रखें। संक्रमित पत्तियों पर नजर रखें और खेत की नियमित सफाई करें।"
            )
            isSpot -> Pair(
                "अधिक प्रभावित पत्तियों को सावधानी से हटाएं। संक्रमित पौधों की सामग्री को स्वस्थ पौधों से दूर रखें।",
                "पौधों के बीच उचित दूरी रखें। खेत की सफाई बनाए रखें और बारिश के बाद पत्तियों की जांच करें।"
            )
            isHealthy -> Pair(
                "इस मॉडल ने पत्ती को रोग-मुक्त श्रेणी में रखा है।",
                "नियमित रूप से पत्तियों की जांच करें और खेत की स्वच्छता बनाए रखें।"
            )
            else -> Pair(
                "रोग की पुष्टि के लिए कृषि विशेषज्ञ से सलाह लें।",
                "फसल की नियमित निगरानी करें।"
            )
        }

        "Kannada" -> when {
            isRust -> Pair(
                "ಸಸ್ಯಗಳ ನಡುವೆ ಸಾಕಷ್ಟು ಅಂತರವಿರಲಿ, ಇದರಿಂದ ಗಾಳಿಯ ಸಂಚಾರ ಉತ್ತಮವಾಗಿರುತ್ತದೆ. ಎಲೆ ಕೊಯ್ಲಿನಲ್ಲಿ ಅನಗತ್ಯ ವಿಳಂಬ ಮಾಡಬೇಡಿ. ಬಾಧಿತ ಎಲೆಗಳನ್ನು ನಿಯಮಿತವಾಗಿ ಪರಿಶೀಲಿಸಿ.",
                "ಉತ್ತಮ ಗಾಳಿಯ ಸಂಚಾರವನ್ನು ಕಾಪಾಡಿಕೊಳ್ಳಿ. ಸೋಂಕಿತ ಎಲೆಗಳನ್ನು ಗಮನಿಸಿ ಮತ್ತು ತೋಟವನ್ನು ಸ್ವಚ್ಛವಾಗಿಡಿ."
            )
            isSpot -> Pair(
                "ತೀವ್ರವಾಗಿ ಬಾಧಿತ ಎಲೆಗಳನ್ನು ಎಚ್ಚರಿಕೆಯಿಂದ ತೆಗೆದುಹಾಕಿ. ಸೋಂಕಿತ ಸಸ್ಯದ ಭಾಗಗಳನ್ನು ಆರೋಗ್ಯಕರ ಸಸ್ಯಗಳಿಂದ ದೂರವಿಡಿ.",
                "ಸಸ್ಯಗಳ ನಡುವೆ ಸೂಕ್ತ ಅಂತರವಿರಲಿ. ತೋಟದ ಸ್ವಚ್ಛತೆಯನ್ನು ಕಾಪಾಡಿ ಮತ್ತು ಮಳೆಯ ನಂತರ ಎಲೆಗಳನ್ನು ಪರಿಶೀಲಿಸಿ."
            )
            isHealthy -> Pair(
                "ಈ ಮಾದರಿಯು ಎಲೆಯನ್ನು ರೋಗಮುಕ್ತ ವರ್ಗದಲ್ಲಿ ಗುರುತಿಸಿದೆ.",
                "ಎಲೆಗಳನ್ನು ನಿಯಮಿತವಾಗಿ ಪರಿಶೀಲಿಸಿ ಮತ್ತು ತೋಟದ ಸ್ವಚ್ಛತೆಯನ್ನು ಕಾಪಾಡಿಕೊಳ್ಳಿ."
            )
            else -> Pair(
                "ರೋಗವನ್ನು ದೃಢೀಕರಿಸಲು ಕೃಷಿ ತಜ್ಞರನ್ನು ಸಂಪರ್ಕಿಸಿ.",
                "ಬೆಳೆಯನ್ನು ನಿಯಮಿತವಾಗಿ ಪರಿಶೀಲಿಸಿ."
            )
        }

        "Telugu" -> when {
            isRust -> Pair(
                "మొక్కల మధ్య తగినంత దూరం ఉంచి గాలి ప్రసరణ మెరుగుపరచండి. ఆకుల కోతలో అనవసరమైన ఆలస్యం చేయవద్దు. ప్రభావిత ఆకులను క్రమం తప్పకుండా పరిశీలించండి.",
                "మంచి గాలి ప్రసరణ ఉండేలా చూడండి. సోకిన ఆకులను గమనిస్తూ తోటను శుభ్రంగా ఉంచండి."
            )
            isSpot -> Pair(
                "తీవ్రంగా ప్రభావితమైన ఆకులను జాగ్రత్తగా తొలగించండి. సోకిన మొక్కల భాగాలను ఆరోగ్యకరమైన మొక్కలకు దూరంగా ఉంచండి.",
                "మొక్కల మధ్య తగినంత దూరం ఉంచండి. తోట పరిశుభ్రతను పాటించండి మరియు వర్షం తర్వాత ఆకులను పరిశీలించండి."
            )
            isHealthy -> Pair(
                "ఈ మోడల్ ఆకును వ్యాధి లేని వర్గంగా గుర్తించింది.",
                "ఆకులను క్రమం తప్పకుండా పరిశీలించండి మరియు తోట పరిశుభ్రతను పాటించండి."
            )
            else -> Pair(
                "వ్యాధిని నిర్ధారించడానికి వ్యవసాయ నిపుణుడిని సంప్రదించండి.",
                "పంటను క్రమం తప్పకుండా పరిశీలించండి."
            )
        }

        else -> when {
            isRust -> Pair(
                "Maintain adequate spacing between plants to improve airflow. Avoid delaying leaf harvesting. Regularly inspect affected leaves.",
                "Maintain good air circulation. Monitor infected leaves and keep the mulberry garden clean."
            )
            isSpot -> Pair(
                "Carefully remove severely affected leaves. Keep infected plant material away from healthy plants.",
                "Maintain adequate plant spacing. Keep the garden clean and inspect leaves after rainfall."
            )
            isHealthy -> Pair(
                "The model has classified this leaf as disease-free.",
                "Inspect leaves regularly and maintain good field hygiene."
            )
            else -> Pair(
                "Consult an agricultural expert to confirm the disease.",
                "Monitor the crop regularly."
            )
        }
    }
}


private fun cleanLlamaResponse(raw: String): String {
    return raw
        // Remove complete chat headers
        .replace(
            Regex("<\\|start_header_id\\|>.*?<\\|end_header_id\\|>"),
            ""
        )
        // Remove remaining special tokens
        .replace(Regex("<\\|[^|]+\\|>"), "")
        .trim()
}

@Composable
fun LlamaLoadTestScreen(context: android.content.Context) {
    val assistant = remember {
        LlamaAssistant(context.applicationContext)
    }

    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Model not loaded") }
    var response by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(false) }
    var generating by remember { mutableStateOf(false) }

    val mainHandler = remember {
        android.os.Handler(android.os.Looper.getMainLooper())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bharat AI - Llama Generation Test")

        Spacer(Modifier.height(16.dp))

        Button(
            enabled = !loaded,
            onClick = {
                status = "Loading model..."
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            assistant.load()
                        }
                        loaded = true
                        status = "Model loaded successfully!"
                    } catch (e: Exception) {
                        status = "Loading error: ${e.message}"
                    }
                }
            }
        ) {
            Text("Load Llama")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            enabled = loaded && !generating,
            onClick = {
                generating = true
                response = ""
                status = "Generating response..."

                val prompt = "Write one short sentence about mulberry leaves."

                scope.launch(Dispatchers.IO) {
                    try {
                        assistant.generate(
                            prompt,

                            object : LlmCallback {

                                override fun onResult(result: String) {
                                    mainHandler.post {
                                        response += result
                                    }
                                }

                                override fun onStats(stats: String) {
                                    mainHandler.post {
                                        response = cleanLlamaResponse(response)
                                        generating = false
                                        status = "Generation completed!"
                                    }
                                }

                                override fun onError(
                                    errorCode: Int,
                                    message: String
                                ) {
                                    mainHandler.post {
                                        generating = false
                                        status = "Generation error $errorCode: $message"
                                    }
                                }
                            }
                        )

                    } catch (e: Exception) {
                        mainHandler.post {
                            status = "Generation error: ${e.message}"
                            generating = false
                        }
                    }
                }
            }
        ) {
            Text("Generate Test Response")
        }

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(12.dp))

        Text("Llama response:")
        Text(response)
    }
}

@Composable
private fun RegionalAdvisoryScreen(
    strings: AppStrings,
    languageCode: String,
    onBack: () -> Unit
){
    val context = LocalContext.current
    val locationDatabase = remember {
        LocationDatabaseHelper(context)
    }

    val advisoryDatabase = remember {
        AdvisoryDatabaseHelper(context)
    }

    var confirmedLocation by remember {
        mutableStateOf("")
    }

    var advisoryItems by remember {
        mutableStateOf<List<AdvisoryItem>>(emptyList())
    }

    var selectedAdvisoryCategory by remember {
        mutableStateOf("All")
    }

    val filteredAdvisories = advisoryItems.filter { advisory ->
        selectedAdvisoryCategory == "All" ||
                advisory.category == selectedAdvisoryCategory
    }

    val indianStates = remember {
        locationDatabase.getStates()
    }
    var selectedTaluk by remember {
        mutableStateOf("")
    }

    var talukDropdownExpanded by remember {
        mutableStateOf(false)
    }

    var villages by remember {
        mutableStateOf<List<Pair<Int, String>>>(emptyList())
    }

    var selectedVillage by remember { mutableStateOf("") }
    var selectedVillageCode by remember { mutableStateOf<Int?>(null) }
    var villageDropdownExpanded by remember { mutableStateOf(false) }

    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationMessage by remember {
        mutableStateOf(strings.locationNotDetected)
    }

    var isGettingLocation by remember {
        mutableStateOf(false)
    }

    var selectedState by remember {
        mutableStateOf("")
    }
    var selectedStateCode by remember {
        mutableStateOf<Int?>(null)
    }

    var districts by remember {
        mutableStateOf<List<Pair<Int, String>>>(emptyList())
    }

    var selectedDistrictCode by remember {
        mutableStateOf<Int?>(null)
    }

    var subDistricts by remember {
        mutableStateOf<List<Pair<Int, String>>>(emptyList())
    }

    var selectedSubDistrictCode by remember {
        mutableStateOf<Int?>(null)
    }

    var stateDropdownExpanded by remember {
        mutableStateOf(false)
    }

    var selectedDistrict by remember {
        mutableStateOf("")
    }

    var districtDropdownExpanded by remember {
        mutableStateOf(false)
    }

    fun fetchCurrentLocation() {
        isGettingLocation = true
        locationMessage = strings.detectingLocation

        val cancellationToken = CancellationTokenSource()

        try {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location ->

                isGettingLocation = false

                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) { addresses ->

                            locationMessage = if (addresses.isNotEmpty()) {
                                val address = addresses[0]

                                """
                Village: ${address.subLocality ?: address.locality ?: "Not available"}
                Taluk: ${address.subAdminArea ?: "Not available"}
                District: ${address.subAdminArea ?: "Not available"}
                State: ${address.adminArea ?: "Not available"}
                """
                            } else {
                                strings.addressNotFound
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        locationMessage = if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]

                            """
            Village: ${address.subLocality ?: address.locality ?: "Not available"}
            Taluk: ${address.subAdminArea ?: "Not available"}
            District: ${address.subAdminArea ?: "Not available"}
            State: ${address.adminArea ?: "Not available"}
            """
                        } else {
                            strings.addressNotFound
                        }
                    }
                } else {
                    locationMessage = strings.unableToDetectLocation
                }

            }.addOnFailureListener { error ->

                isGettingLocation = false
                locationMessage = "Location error: ${error.localizedMessage}"
            }

        } catch (e: SecurityException) {
            isGettingLocation = false
            locationMessage = strings.locationPermissionRequired
        }
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                fetchCurrentLocation()
            } else {
                locationMessage = strings.locationPermissionDenied
            }
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(strings.backHome, color = Green)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "🌾 ${strings.regionalAdvisoryTitle}",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.regionalAdvisoryDescription,
            color = GreyText
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val fineGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val coarseGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (fineGranted || coarseGranted) {
                    fetchCurrentLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green
            )
        ) {
            Text("📍 ${strings.currentLocation}")
        }
        Text(
            text = locationMessage,
            color = GreyText,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                confirmedLocation = ""
                stateDropdownExpanded = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (selectedState.isEmpty()) {
                    strings.selectLocation
                } else {
                    "${strings.stateLabel}: $selectedState"
                }
            )
        }

        DropdownMenu(
            expanded = stateDropdownExpanded,
            onDismissRequest = {
                stateDropdownExpanded = false
            },
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            indianStates.forEach { state ->
                DropdownMenuItem(
                    text = {
                        Text(state.second)
                    },
                    onClick = {
                        selectedStateCode = state.first
                        selectedState = state.second

                        districts = locationDatabase.getDistricts(state.first)
                        subDistricts = emptyList()
                        selectedSubDistrictCode = null
                        villages = emptyList()
                        selectedVillage = ""
                        selectedVillageCode = null
                        advisoryItems = emptyList()
                        confirmedLocation = ""

                        selectedDistrict = ""
                        selectedDistrictCode = null
                        selectedTaluk = ""

                        districtDropdownExpanded = false
                        talukDropdownExpanded = false

                        districtDropdownExpanded = false
                        talukDropdownExpanded = false
                        stateDropdownExpanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                confirmedLocation = ""
                districtDropdownExpanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = districts.isNotEmpty()
        ) {
            Text(
                text = if (selectedDistrict.isEmpty()) {
                    strings.selectDistrict
                } else {
                    "${strings.districtLabel}: $selectedDistrict"
                }
            )
        }

        DropdownMenu(
            expanded = districtDropdownExpanded,
            onDismissRequest = {
                districtDropdownExpanded = false
            },
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            districts.forEach { district ->
                DropdownMenuItem(
                    text = {
                        Text(district.second)
                    },
                    onClick = {
                        selectedDistrictCode = district.first
                        selectedDistrict = district.second

                        subDistricts = locationDatabase.getSubDistricts(
                            selectedStateCode!!,
                            district.first
                        )

                        selectedSubDistrictCode = null

                        selectedTaluk = ""
                        villages = emptyList()
                        selectedVillage = ""
                        selectedVillageCode = null
                        advisoryItems = emptyList()
                        confirmedLocation = ""
                        talukDropdownExpanded = false
                        districtDropdownExpanded = false
                    }
                )
            }
        }

        if (
            selectedState.isNotEmpty() &&
            districts.isEmpty()
        ) {
            Text(
                text = "${strings.noDistrictData} $selectedState.",
                color = GreyText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }


        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                confirmedLocation = ""
                talukDropdownExpanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = subDistricts.isNotEmpty()
        ) {
            Text(
                text = if (selectedTaluk.isEmpty()) {
                    strings.selectTaluk
                } else {
                    "${strings.talukLabel}: $selectedTaluk"
                }
            )
        }

        DropdownMenu(
            expanded = talukDropdownExpanded,
            onDismissRequest = {
                talukDropdownExpanded = false
            },
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            subDistricts.forEach { subDistrict ->
                DropdownMenuItem(
                    text = {
                        Text(subDistrict.second)
                    },
                    onClick = {
                        selectedSubDistrictCode = subDistrict.first
                        selectedTaluk = subDistrict.second

                        val stateCode = selectedStateCode
                        val districtCode = selectedDistrictCode

                        if (stateCode != null && districtCode != null) {
                            villages = locationDatabase.getVillages(
                                stateCode,
                                districtCode,
                                subDistrict.first
                            )
                        }

                        selectedVillage = ""
                        selectedVillageCode = null
                        villageDropdownExpanded = false
                        talukDropdownExpanded = false
                    }
                )
            }
        }

        if (
            selectedDistrict.isNotEmpty() &&
            subDistricts.isEmpty()
        ) {
            Text(
                text = "${strings.noTalukData} $selectedDistrict.",
                color = GreyText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                confirmedLocation = ""
                villageDropdownExpanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = villages.isNotEmpty()
        ) {
            Text(
                text = if (selectedVillage.isEmpty()) {
                    strings.selectVillage
                } else {
                    "${strings.villageLabel}: $selectedVillage"
                }
            )
        }

        DropdownMenu(
            expanded = villageDropdownExpanded,
            onDismissRequest = {
                villageDropdownExpanded = false
            },
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            villages.forEach { village ->
                DropdownMenuItem(
                    text = { Text(village.second) },
                    onClick = {
                        selectedVillageCode = village.first
                        selectedVillage = village.second
                        villageDropdownExpanded = false
                    }
                )
            }
        }

        if (
            selectedTaluk.isNotEmpty() &&
            villages.isEmpty()
        ) {
            Text(
                text = "${strings.noVillageData} $selectedTaluk.",
                color = GreyText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                confirmedLocation = "$selectedVillage, $selectedTaluk, $selectedDistrict, $selectedState"

                val stateCode = selectedStateCode
                val districtCode = selectedDistrictCode

                advisoryItems = advisoryDatabase.getVerifiedAdvisories(
                    stateCode = stateCode,
                    districtCode = districtCode,
                    languageCode = languageCode
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedState.isNotEmpty() &&
                    selectedDistrict.isNotEmpty() &&
                    selectedTaluk.isNotEmpty() &&
                    selectedVillage.isNotEmpty()
        ) {
            Text(strings.confirmLocation)
        }
        if (confirmedLocation.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = 	strings.confirmedLocation,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = confirmedLocation)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = 	strings.farmingAdvisories,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (confirmedLocation.isNotEmpty()) {
                TextButton(
                    onClick = {
                        advisoryItems = advisoryDatabase.getVerifiedAdvisories(
                            stateCode = selectedStateCode,
                            districtCode = selectedDistrictCode
                        )
                    }
                ) {
                    Text("↻ ${strings.refresh}", color = Green)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (confirmedLocation.isNotEmpty()) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        selectedAdvisoryCategory = "All"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAdvisoryCategory == "All")
                            Green else Color.LightGray
                    )
                ) {
                    Text(strings.all)
                }

                Button(
                    onClick = {
                        selectedAdvisoryCategory = "Disease Management"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAdvisoryCategory == "Disease Management")
                            Green else Color.LightGray
                    )
                ) {
                    Text(strings.diseaseManagement)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (confirmedLocation.isNotEmpty()) {

            if (filteredAdvisories.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (selectedAdvisoryCategory == "All")
                                strings.noAdvisories
                            else
                                strings.noCategoryAdvisories,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = strings.regionalEmptyDescription
                        )
                    }
                }

            } else {

                filteredAdvisories.forEach { advisory ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = advisory.category.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Green
                            )

                            Text(
                                text = advisory.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = advisory.description)

                            if (!advisory.symptoms.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${strings.symptoms}: ${advisory.symptoms}"
                                )
                            }

                            if (!advisory.recommendation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${strings.recommendation}: ${advisory.recommendation}"
                                )
                            }

                            if (!advisory.sourceOrganization.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${strings.source}: ${advisory.sourceOrganization}",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }

                            if (!advisory.sourceTitle.isNullOrBlank()) {
                                Text(
                                    text = "${strings.reference}: ${advisory.sourceTitle}",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}
data class LocationData(
    val state: String,
    val districts: Map<String, Map<String, List<String>>>
)