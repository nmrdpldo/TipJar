package com.example.tip_jar.ui.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tip_jar.utils.TestTags
import com.example.tip_jar.database.entity.TipHistory
import com.example.tip_jar.ui.theme.BackButtonGray
import com.example.tip_jar.ui.theme.ImageBGColor
import com.example.tip_jar.utils.Utils
import com.example.tip_jar.viewmodel.TipJarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun SavedPaymentsScreen(
    navController: NavController,
    viewModel: TipJarViewModel = hiltViewModel()
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .testTag(TestTags.SavePaymentScreen)
    ) {
        BackButtonSection(navController)
        Divider()
        SavedPaymentsLazyColumn(viewModel)
    }
}

@Composable
fun BackButtonSection (navController: NavController){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Icon(
            Icons.Rounded.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .clickable {
                    navController.popBackStack()
                },
            tint = BackButtonGray
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "SAVED PAYMENTS",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp)

    }
}

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    viewModel: TipJarViewModel = hiltViewModel(),
    onSearch: (String) -> Unit = {}
) {
    val text by remember {
        viewModel.query
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                viewModel.query.value = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }
        )
        if(isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun SavedPaymentsLazyColumn(
    viewModel: TipJarViewModel = hiltViewModel()
){
    val paymentHistory = remember { viewModel.paymentHistory }

    CustomSearchBar(
        hint = "Search...",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 0.dp),
        viewModel = viewModel
    ) {
        viewModel.searchPaymentList()
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)){
        items (paymentHistory.value.size) { currIndex ->
            SavedPaymentsEntry(
                paymentHistory.value[currIndex],
                viewModel
            )
        }
    }
}

@Composable
fun SavedPaymentsEntry(
    entry : TipHistory,
    viewModel: TipJarViewModel = hiltViewModel()
){
    val showDialog = remember { mutableStateOf(false) }
    val showConfirmDeleteDialog = remember { mutableStateOf(false) }
    if(showDialog.value){
        ShowPaymentDetails(
            entry,
            onDismiss = { showDialog.value = false }
        )
    }
    if(showConfirmDeleteDialog.value){
        DeleteConfirmDialog(
            entry,
            onDismiss = { showConfirmDeleteDialog.value = false },
            viewModel
        )
    }



    Row (verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable {
                showDialog.value = true
            }){
        Column (modifier = Modifier
            .weight(1f),
            verticalArrangement = Arrangement.Center){
            Text(
                text = entry.timestamp.toDateString("yyyy MMMM dd"),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier
                .size(6.dp))
            Row (verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "$" +  Utils.roundOffTwoDecimals(entry.amount.toDouble()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier
                    .size(16.dp))
                Text(
                    text = "Tip:  $" + Utils.roundOffTwoDecimals(entry.totalTip),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = BackButtonGray)
            }
        }
        if(entry.imageData.isNotEmpty()){
            Image(
                painter = rememberAsyncImagePainter(
                    model = Utils.converterStringToBitmap(entry.imageData)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(53.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(ImageBGColor))
        }else{
            Icon(
                Icons.Outlined.Image,
                modifier = Modifier
                    .size(53.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(ImageBGColor),
                contentDescription = null,
                tint = Color.White)
        }
        Spacer(modifier = Modifier
            .size(16.dp))
        Icon(
            Icons.Rounded.DeleteForever,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier
                .clickable {
                    showConfirmDeleteDialog.value = true
                })
    }
}

@Composable
fun DeleteConfirmDialog(
    entry: TipHistory,
    onDismiss: () -> Unit,
    viewModel: TipJarViewModel = hiltViewModel()
){
    val context = LocalContext.current

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = {
            onDismiss()
        },
        text = {
            Text(
                "Are you sure you want to delete this item?",
                color = Color.Black
            )
        },
        confirmButton = {
            Text(
                "Confirm",
                color = Color.Black,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        CoroutineScope(Dispatchers.Main).launch {
                            val delJob = launch {
                                viewModel.deletePayment(entry)
                            }
                            delJob.join()
                            onDismiss()
                            Toast
                                .makeText(context, "Successfully Deleted!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
        },
        dismissButton = {
            Text(text = "Cancel",
                color = Color.Black,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onDismiss()
                    })
        }
    )
}

@Composable
fun ShowPaymentDetails(
    entry: TipHistory,
    onDismiss: () -> Unit
){
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box (
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent)
                .padding(32.dp, 24.dp, 32.dp, 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                if(entry.imageData.isNotEmpty()){
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = Utils.converterStringToBitmap(entry.imageData)
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(shape = RoundedCornerShape(12.dp)))
                }
                Spacer(modifier = Modifier
                    .size(16.dp))
                Box(modifier = Modifier
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color.White)
                ){
                    Column (modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                        verticalArrangement = Arrangement.Center){
                        Text(
                            text = entry.timestamp.toDateString("yyyy MMMM dd"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp)
                        Spacer(modifier = Modifier
                            .size(6.dp))
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween){
                            Text(
                                text = "$" +  Utils.roundOffTwoDecimals(entry.amount.toDouble()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp)
                            Spacer(modifier = Modifier
                                .size(24.dp))
                            Text(
                                text = "Tip:  $" + Utils.roundOffTwoDecimals(entry.totalTip),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = BackButtonGray)
                        }
                    }
                }
            }
        }

    }
}

fun Long.toDateString(dateFormat: String): String {
    val sdf = SimpleDateFormat(dateFormat, Locale.ENGLISH)
    val date = Date(this)
    return sdf.format(date)
}

