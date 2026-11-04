package com.bharatai.cropassistant

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

// Application colours
private val Green = Color(0xFF166534)
private val LightGreen = Color(0xFFE8F5E9)
private val Background = Color(0xFFF8FAF7)
private val DarkText = Color(0xFF17251B)
private val GreyText = Color(0xFF647067)

// All text displayed in the application is kept in one place.
// Add future screen translations to this data class and translationsFor().
private data class AppStrings(
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
    val uploadImage: String
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
            uploadImage = "तस्वीर अपलोड करें"
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
            uploadImage = "ಚಿತ್ರವನ್ನು ಅಪ್‌ಲೋಡ್ ಮಾಡಿ"
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
            uploadImage = "చిత్రాన్ని అప్‌లోడ్ చేయండి"
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
            uploadImage = "Upload Image"
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

    val strings = translationsFor(selectedLanguage)

    if (selectedTab == "Scan") {
        CropScannerScreen(
            strings = strings,
            onBack = {
                selectedTab = "Home"
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
                            selectedTab = "Scan"
                            message = ""
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
    onBack: () -> Unit
){
    val context = LocalContext.current

    var selectedImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                selectedImage = BitmapFactory.decodeStream(stream)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedImage = bitmap
        }
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

        Text(
            text = "🌿",
            fontSize = 55.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "strings.scannerTitle",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "strings.scannerDescription",
            fontSize = 14.sp,
            color = GreyText
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {cameraLauncher.launch(null) },
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