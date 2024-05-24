package com.example.legalease.ui.client.addCaseScreen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.example.compose.LegalEaseTheme
//import com.example.legalease.ui.theme.LegalEaseTheme
import com.example.legalease.ui.viewModels.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.sqrt

//@Composable
//fun PdfViewer(
//    modifier: Modifier = Modifier,
//    uri: Uri,
//    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
//) {
//    val rendererScope = rememberCoroutineScope()
//    val mutex = remember { Mutex() }
//    val renderer by produceState<PdfRenderer?>(null, uri) {
//        rendererScope.launch(Dispatchers.IO) {
//            val input = ParcelFileDescriptor.open(uri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
//            value = PdfRenderer(input)
//        }
//        awaitDispose {
//            val currentRenderer = value
//            rendererScope.launch(Dispatchers.IO) {
//                mutex.withLock {
//                    currentRenderer?.close()
//                }
//            }
//        }
//    }
//    val context = LocalContext.current
//    val imageLoader = LocalContext.current.imageLoader
//    val imageLoadingScope = rememberCoroutineScope()
//    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
//        val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
//        val height = (width * sqrt(2f)).toInt()
//        val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }
//        LazyColumn(
//            verticalArrangement = verticalArrangement
//        ) {
//            items(
//                count = pageCount,
//                key = { index -> "$uri-$index" }
//            ) { index ->
//                val cacheKey = MemoryCache.Key("$uri-$index")
//                val cacheValue: Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.bitmap
//
//                var bitmap: Bitmap? by remember { mutableStateOf(cacheValue) }
//                if (bitmap == null) {
//                    DisposableEffect(uri, index) {
//                        val job = imageLoadingScope.launch(Dispatchers.IO) {
//                            val destinationBitmap =
//                                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                            mutex.withLock {
//                                Log.d("PdfGenerator", "Loading PDF $uri - page $index/$pageCount")
//                                if (!coroutineContext.isActive) return@launch
//                                try {
//                                    renderer?.let {
//                                        it.openPage(index).use { page ->
//                                            page.render(
//                                                destinationBitmap,
//                                                null,
//                                                null,
//                                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
//                                            )
//                                        }
//                                    }
//                                } catch (e: Exception) {
//                                    //Just catch and return in case the renderer is being closed
//                                    return@launch
//                                }
//                            }
//                            bitmap = destinationBitmap
//                        }
//                        onDispose {
//                            job.cancel()
//                        }
//                    }
//                    Box(
//                        modifier = Modifier
//                            .background(Color.White)
//                            .aspectRatio(1f / sqrt(2f))
//                            .fillMaxWidth()
//                    )
//                } else { //bitmap != null
//                    val request = ImageRequest.Builder(context)
//                        .size(width, height)
//                        .memoryCacheKey(cacheKey)
//                        .data(bitmap)
//                        .build()
//
//                    Image(
//                        modifier = Modifier
//                            .background(Color.White)
//                            .aspectRatio(1f / sqrt(2f))
//                            .fillMaxWidth(),
//                        contentScale = ContentScale.Fit,
//                        painter = rememberImagePainter(request),
//                        contentDescription = "Page ${index + 1} of $pageCount"
//                    )
//                }
//            }
//        }
//    }
//}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddCaseScreen(modifier: Modifier = Modifier, lawyerId: String = "") {
    val vm: AuthViewModel = hiltViewModel()
    val addCaseViewModel: AddCaseViewModel = hiltViewModel()
    val addCaseUiState by addCaseViewModel.addCaseUiState.collectAsState()
    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = addCaseUiState.caseType,
            onValueChange = { addCaseViewModel.updateTitle(it) },
            label = {
                Text(
                    text = "Case Type"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = addCaseUiState.languagePreference,
            onValueChange = { addCaseViewModel.updateLanguagePreference(it) },
            label = {
                Text(
                    text = "Language Preference"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = addCaseUiState.description,
            onValueChange = { addCaseViewModel.updateDescription(it) },
            label = {
                Text(
                    text = "Description"
                )
            },
            placeholder = {
                Text(
                    text = "Enter detailed description of your case",
                    color = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            minLines = 10,
            maxLines = 10
        )
        Spacer(modifier = Modifier.height(16.dp))


        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.dataString
                    Log.d("PDF", "$data")
                    result.data?.data?.let { pdfUri ->
                        addCaseViewModel.pdfUriList += pdfUri
                    }
                }
            }

        Button(onClick = {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            launcher.launch(intent)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Upload pdf")
        }

        Button(onClick = {
            addCaseViewModel.uploadPdfToDataBase()
            addCaseViewModel.sendCase(lawyerId = lawyerId)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Submit")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddCaseScreenPreview(modifier: Modifier = Modifier) {
    LegalEaseTheme {
        AddCaseScreen()
    }
}