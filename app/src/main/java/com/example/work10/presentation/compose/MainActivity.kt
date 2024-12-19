package com.example.work10.presentation.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.work10.R
import com.example.work10.presentation.viewmodel.CatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val catViewModel: CatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatScreen(catViewModel = catViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatScreen(catViewModel: CatViewModel) {
    val context = LocalContext.current
    val catImageUrl by catViewModel.catImageUrl.observeAsState()
    val saveResult by catViewModel.saveResult.observeAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Сайд-эффект для скачивания и сохранения изображения при появлении нового URL
    LaunchedEffect(catImageUrl) {
        catImageUrl?.let { url ->
            catViewModel.downloadAndSaveImage(url)
        }
    }

    // Сайд-эффект для отображения сообщений Toast при изменении результата сохранения
    LaunchedEffect(saveResult) {
        saveResult?.let { success ->
            val message = if (success) {
                "Изображение успешно сохранено!"
            } else {
                "Ошибка при сохранении изображения"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // UI с Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(onItemSelected = { item ->
                Toast.makeText(context, "Вы выбрали: $item", Toast.LENGTH_SHORT).show()
            })
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Кат изображение") },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Открыть Drawer")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (catImageUrl != null) {
                        // Загрузка изображения с помощью Coil
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(catImageUrl)
                                .placeholder(R.drawable.gear_spinner)
                                .error(R.drawable.error)
                                .build(),
                            contentDescription = "Cat Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(bottom = 16.dp)
                        )
                    }

                    //Button(onClick = { catViewModel.fetchCat() }) {
                    //    Text(text = "fetch cat")
                    //}

                    Button(onClick = { catViewModel.downloadImageUsingWorkManager("https://example.com/cat.jpg") }) {
                        Text(text = "Загрузить изображение через WorkManager")
                    }
                }
            }
        )
    }
}

@Composable
fun DrawerContent(onItemSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Меню", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = AnnotatedString("Home"),
            onClick = { onItemSelected("Home") }
        )
        ClickableText(
            text = AnnotatedString("Profile"),
            onClick = { onItemSelected("Profile") }
        )
        ClickableText(
            text = AnnotatedString("Settings"),
            onClick = { onItemSelected("Settings") }
        )
    }
}
