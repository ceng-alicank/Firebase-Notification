package com.notiapp.notification
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.notiapp.notification.ui.theme.PushNotificationPermisionDemoTheme
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PushNotificationPermisionDemoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomePage(intent = intent)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(intent: Intent){
    val context = LocalContext.current
    val gckTokenKey = stringPreferencesKey("gcm_token")

    val fcmToken = flow<String> {
        context.dataStore.data.map {
            it[gckTokenKey]
        }.collect(collector = {
            if(it != null){
                this.emit(it)
            }
        })
    }.collectAsState(initial = "")

    var notificationTitle = remember {
        mutableStateOf(
            if (intent.hasExtra("title")) {
                intent.getStringExtra("title")
            } else {
                ""
            }
        )
    }

    var notificationBody = remember {
        mutableStateOf(
            if (intent.hasExtra("body")) {
                intent.getStringExtra("body")
            } else {
                ""
            }
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Cloud Messaging Project",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.height(15.dp))
        if(notificationTitle.value!!.isEmpty() && notificationBody.value!!.isEmpty()){
            Text(text = "Notification Box Empty", modifier = Modifier.padding(15.dp))
        }
        else {
            Text(
                text = "Data Title",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Text(
                text = notificationTitle.value!!,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Data Body",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Text(
                text = notificationBody.value!!,
                fontSize = 17.sp
            )
        }
        Spacer(modifier = Modifier.height(300.dp))


    }
}

