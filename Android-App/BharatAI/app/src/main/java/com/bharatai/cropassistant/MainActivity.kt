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
    val stateLabel: String,
    val editProfile: String,
    val saveProfile: String,
    val phoneNumberError: String

)

private fun translationsFor(language: String): AppStrings {
    return when (language) {
        "Hindi" -> AppStrings(
            appSubtitle = "स्मार्ट कृषि सहायक",
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
            phoneNumberError = "फ़ोन नंबर में ठीक 10 अंक होने चाहिए।"
        )

        "Kannada" -> AppStrings(
            appSubtitle = "ಸ್ಮಾರ್ಟ್ ಕೃಷಿ ಸಹಾಯಕ",
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
            phoneNumberError = "ದೂರವಾಣಿ ಸಂಖ್ಯೆಯಲ್ಲಿ ನಿಖರವಾಗಿ 10 ಅಂಕಿಗಳಿರಬೇಕು."
        )

        "Telugu" -> AppStrings(
            appSubtitle = "స్మార్ట్ వ్యవసాయ సహాయకుడు",
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
            villageLabel = "గ్రామం",
            districtLabel = "జిల్లా",
            stateLabel = "రాష్ట్రం",
            editProfile = "ప్రొఫైల్‌ను సవరించండి",
            saveProfile = "ప్రొఫైల్‌ను సేవ్ చేయండి",
            phoneNumberError = "ఫోన్ నంబర్‌లో ఖచ్చితంగా 10 అంకెలు ఉండాలి."
        )

        else -> AppStrings(
            appSubtitle = "Smart Farming Assistant",
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
            phoneNumberError = "Phone number must contain exactly 10 digits."
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
                    Log.w("BharatAI_Debug", "Analysis already running. Ignoring duplicate request.")
                } else {

                    Log.d("BharatAI_Debug", "onAnalyze callback started")

                    selectedCropImage = image
                    isAnalyzing = true
                    analysisError = null
                    prediction = null

                val existingClassifier = classifier

                scope.launch {
                    try {
                        Log.d("BharatAI_Debug", "Starting classifier")

                        val (loadedClassifier, result) =
                            withContext(Dispatchers.Default) {
                                val activeClassifier =
                                    existingClassifier ?: MulberryClassifier(context)

                                Log.d("BharatAI_Debug", "Calling predict()")

                                activeClassifier to activeClassifier.predict(image)
                            }

                        Log.d(
                            "BharatAI_Debug",
                            "Prediction completed: ${result.diseaseName}"
                        )

                        classifier = loadedClassifier
                        prediction = result
                        selectedTab = "Results"

                    } catch (e: Exception) {
                        Log.e("BharatAI_Debug", "Classification failed", e)

                        analysisError =
                            e.localizedMessage ?: "Unable to analyze this image."

                        selectedTab = "Results"

                    } finally {
                        isAnalyzing = false
                        Log.d("BharatAI_Debug", "Analysis finished")
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
                    text = "BHARAT AI",
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

            // AI Assistant Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTab = "Assistant"
                        message = strings.assistantSelected
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🤖", fontSize = 35.sp)

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = strings.assistantTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green
                        )

                        Text(
                            text = strings.assistantSubtitle,
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
                emoji = "🤖",
                title = strings.assistant,
                selected = selectedTab == "Assistant"
            ) {
                selectedTab = "Assistant"
                message = strings.assistantSelected
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
    isAnalyzing: Boolean,
    analysisError: String?,
    onTreatmentClick: () -> Unit,
    onBack: () -> Unit
) {
    val appContext = androidx.compose.ui.platform.LocalContext.current

    val llamaAssistant = remember(appContext) {
        LlamaAssistant(appContext.applicationContext)
    }

    val scope = rememberCoroutineScope()

    var llamaLoaded by remember {
        mutableStateOf(false)
    }

    var isGeneratingExplanation by remember {
        mutableStateOf(false)
    }

    var llamaExplanation by remember {
        mutableStateOf("")
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
        selectedLanguage,
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




        val prompt = """
    <|start_header_id|>system<|end_header_id|>

    You are Bharat AI, an assistant for mulberry farmers.
    Use simple English.
    The disease name provided is a classifier label,
    NOT a dataset name.
    Explain the predicted condition directly.
    Explain that confidence is not a guarantee.
    Give one simple and safe recommendation.
    Do not discuss datasets, AI models, or training data.
    Do not invent medicines or treatments.
    Use exactly 2 short sentences and fewer than 35 words.
    <|eot_id|><|start_header_id|>user<|end_header_id|>

    Classifier result: ${currentPrediction.diseaseName}
    Confidence: ${(currentPrediction.confidence * 100).toInt()}%.
    Explain this result to the farmer.
    <|eot_id|><|start_header_id|>assistant<|end_header_id|>
""".trimIndent()


        llamaExplanation = ""
        isGeneratingExplanation = true
        llamaStatus = "Preparing Llama model..."

        try {
            withContext(Dispatchers.IO) {
                if (!llamaLoaded) {
                    llamaAssistant.load()
                }
            }

            llamaLoaded = true
            llamaStatus = "Generating farmer-friendly explanation..."

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
                                llamaStatus = "Explanation generated!"
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
                        Text("Analyzing leaf image...")
                    }

                    analysisError != null -> {
                        Text(
                            text = "Analysis failed: $analysisError",
                            color = Color.Red
                        )
                    }

                    prediction != null -> {
                        Text(
                            text = "${strings.diseaseLabel} ${prediction.diseaseName}",
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${strings.confidenceLabel} ${
                                "%.2f".format(
                                    java.util.Locale.US,
                                    prediction.confidence * 100
                                )
                            }%",
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "AI model result. Please confirm with a crop expert.",
                            fontSize = 13.sp,
                            color = GreyText
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (diseaseInfo != null) {

                            Text(
                                text = "Disease Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Verified symptoms",
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )

                            if (diseaseInfo.symptoms.isEmpty()) {
                                Text("Verified symptom information is not available yet.")
                            } else {
                                diseaseInfo.symptoms.forEach { symptom ->
                                    Text("• $symptom", color = DarkText)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Treatment: Not yet verified",
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Safety: ${diseaseInfo.safetyNote}",
                                color = Color(0xFFB45309)
                            )

                        } else if (prediction.diseaseName == "Disease Free leaves") {
                            Text(
                                text = "No disease was detected by the AI classifier.",
                                color = Green,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Continue regular crop monitoring. This result does not guarantee that the plant is completely disease-free.",
                                color = GreyText
                            )
                        } else {
                            Text(
                                text = "No verified advisory information is available for this classification yet.",
                                color = GreyText
                            )
                        }
                    }

                    else -> {
                        Text("No prediction available.")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Bharat AI Explanation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Green
                )

                Text(
                    text = llamaStatus,
                    color = GreyText,
                    fontSize = 13.sp
                )

                if (isGeneratingExplanation) {
                    CircularProgressIndicator(
                        color = Green,
                        modifier = Modifier.padding(8.dp)
                    )
                }


                if (llamaExplanation.isNotBlank()) {
                    Text(
                        text = llamaExplanation,
                        color = DarkText,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

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
    confidence: Float,
    symptoms: List<String>,
    safetyNote: String,
    language: String
): String {

    val isHealthy = disease.equals(
        "Disease Free leaves",
        ignoreCase = true
    )

    val symptomText = if (symptoms.isEmpty()) {
        "No verified reference symptoms available."
    } else {
        symptoms.take(3).joinToString("; ")
    }

    val shortSafetyNote = safetyNote.take(120)

    return """
        <|begin_of_text|><|start_header_id|>system<|end_header_id|>
        You are Bharat AI, an assistant for mulberry farmers.
        Explain the diagnosis in one simple paragraph in $language.
        Use only the supplied information. Do not invent symptoms,
        claim reference symptoms were detected in the image,
        or recommend unverified treatments. Include the safety note.
        <|eot_id|><|start_header_id|>user<|end_header_id|>
        Crop: Mulberry
        Disease: $disease
        Confidence: ${"%.2f".format(java.util.Locale.US, confidence * 100)}%
        Reference symptoms: $symptomText
        Safety: $shortSafetyNote
        ${if (isHealthy) "Explain that no disease was detected, but this does not guarantee the whole plant is disease-free." else "Explain the predicted disease and its reference symptoms."}
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