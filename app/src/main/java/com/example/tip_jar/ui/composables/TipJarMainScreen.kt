package com.example.tip_jar.ui.composables

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tip_jar.R
import com.example.tip_jar.utils.TestTags
import com.example.tip_jar.ui.theme.CheckboxLightGray
import com.example.tip_jar.ui.theme.DarkOrangeCustom
import com.example.tip_jar.ui.theme.OrangeCustom
import com.example.tip_jar.utils.Utils
import com.example.tip_jar.viewmodel.TipJarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Composable
fun TipJarMainScreen(
    navController: NavController,
    viewModel: TipJarViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopSection(navController)
        BodySection(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewModel
        )
        SavePaymentButtonSection(
            navController,
            viewModel)
    }
}

@Composable
fun TopSection(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 20.dp, 20.dp, 0.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.tipjar_logo),
            contentDescription = null,
            modifier = Modifier
                .size(114.dp, 29.dp)
                .align(Alignment.Center)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_history),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp, 24.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    navController.navigate(
                        "saved_payments_screen"
                    )
                }
        )
    }
}

@Composable
fun BodySection(
    modifier: Modifier = Modifier,
    viewModel: TipJarViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    Column (modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(24.dp, 24.dp, 24.dp, 12.dp)
                .verticalScroll(scrollState)
        ) {
            EnterAmountSection(viewModel)
            Spacer(modifier = Modifier.size(30.dp))
            PeopleCountSection(viewModel)
            Spacer(modifier = Modifier.size(30.dp))
            TipPercentSection(viewModel)
            Spacer(modifier = Modifier.size(30.dp))
            TotalSection(viewModel)
        }
    }

}

@Composable
fun EnterAmountSection(
    viewModel: TipJarViewModel = hiltViewModel()
) {
    val amountErrorVisibility = remember { viewModel.amountErrorVisible }
    Text(
        text = "Enter Amount",
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.size(8.dp))
    if(amountErrorVisibility.value){
        Text(
            text = "Amount is invalid.",
            color = Color.Red,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
    }
    val (amountValue, setValue) = remember { viewModel.amountValue }
    Box(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp, 0.dp, 12.dp, 0.dp)
            .height(80.dp)
            .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier
                .background(Color.Transparent)
                .wrapContentHeight(unbounded = true)
                .testTag(TestTags.AmountValueTestTags),
            leadingIcon = {
                Icon(
                    Icons.Rounded.AttachMoney,
                    contentDescription = null,
                    tint = OrangeCustom
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Rounded.AttachMoney,
                    contentDescription = null,
                    tint = Color.Transparent
                )
            },
            value = TextFieldValue(
                text = amountValue,
                selection = TextRange(amountValue.length)
            ),
            onValueChange = {
                setValue(it.text)
                viewModel.calculateTotal()
            },
            singleLine = true,
            placeholder = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "100.00",
                    color = Color.LightGray,
                    fontSize = 42.sp,
                    textAlign = TextAlign.Center
                    )
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 42.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                selectionColors = TextSelectionColors(Color.Transparent,Color.Transparent)
            )
        )
    }
}

@Composable
fun PeopleCountSection(
    viewModel : TipJarViewModel = hiltViewModel()
) {
    val peopleCount = remember { viewModel.peopleCount }
    Text(
        text = "How many people?",
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.size(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .shadow(2.dp, CircleShape)
                .background(Color.White, CircleShape)
                .size(71.dp)
                .clickable {
                    peopleCount.intValue++
                    viewModel.calculateTotal()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                ,
                tint = OrangeCustom)
        }
        Text(text = peopleCount.intValue.toString(),
            fontSize = 42.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .testTag(TestTags.PeopleCountTestTags))
        Box(
            modifier = Modifier
                .shadow(2.dp, CircleShape)
                .background(Color.White, CircleShape)
                .size(71.dp)
                .clickable {
                    if (peopleCount.intValue > 1) {
                        peopleCount.intValue--
                        viewModel.calculateTotal()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Remove,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp),
                tint = OrangeCustom)
        }
    }

}

@Composable
fun TipPercentSection(
    viewModel : TipJarViewModel = hiltViewModel()
){
    val tipErrorVisible = remember {viewModel.tipErrorVisible}
    val maxLength = 10
    val (tipValue, setValue) = remember { viewModel.tipValue }
    Text(
        text = "% TIP",
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.size(8.dp))
    if(tipErrorVisible.value){
        Text(
            text = "Tip percent is invalid.",
            color = Color.Red,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
    }
    Box(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp, 0.dp, 12.dp, 0.dp)
    ) {
        TextField(
            modifier = Modifier
                .background(Color.Transparent)
                .wrapContentHeight(unbounded = true)
                .testTag(TestTags.TipValueTestTags),
            leadingIcon = {
                Icon(
                    Icons.Rounded.Percent,
                    contentDescription = null,
                    tint = Color.Transparent
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Rounded.Percent,
                    contentDescription = null,
                    tint = OrangeCustom
                )
            },
            value = TextFieldValue(
                text = tipValue,
                selection = TextRange(tipValue.length)
            ),
            onValueChange = {
                if (it.text.length <= maxLength) {
                    setValue(it.text)
                    viewModel.calculateTotal()
                }
            },
            singleLine = true,
            placeholder = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "10",
                    color = Color.LightGray,
                    fontSize = 42.sp,
                    textAlign = TextAlign.Center
                )
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 42.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                selectionColors = TextSelectionColors(Color.Transparent,Color.Transparent)
            )
        )
    }
}

@Composable
fun TotalSection(
    viewModel: TipJarViewModel = hiltViewModel()
){
    val totalTip = remember { viewModel.totalTip }
    val perPerson = remember { viewModel.perPerson }
    Column(modifier = Modifier
        .fillMaxWidth()) {
        Row (modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Total Tip",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary)
            Text(text = "$${Utils.roundOffTwoDecimals(totalTip.doubleValue)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row (modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Per Person",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag(TestTags.TotalTipTestTags))
            Text(text = "$${Utils.roundOffTwoDecimals(perPerson.doubleValue)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag(TestTags.PerPersonTestTags))
        }

    }
}

@Composable
fun SavePaymentButtonSection(
    navController: NavController,
    viewModel : TipJarViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val isTakePhotoChecked = remember { viewModel.isTakePhotoChecked }
    val imageData = remember { viewModel.imageData }
    val amountValue = remember { viewModel.amountValue }
    val tipValue = remember { viewModel.tipValue }
    val amountErrorVisible = remember { viewModel.amountErrorVisible }
    val tipErrorVisible = remember { viewModel.tipErrorVisible }

    val file = Utils.createImageFile(context = context)
    val imageUri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if(it){
                if(!viewModel.amountValidation(amountValue.value) ||
                    !viewModel.tipValidation(tipValue.value)){
                    amountErrorVisible.value = !viewModel.amountValidation(amountValue.value)
                    tipErrorVisible.value = !viewModel.tipValidation(tipValue.value)
                }else if(imageUri.path?.isEmpty() == true){
                    Toast.makeText(context,"Unable to capture image. Please try again.", Toast.LENGTH_LONG).show()
                }else{
                    viewModel.amountErrorVisible.value = false
                    viewModel.tipErrorVisible.value = false
                    CoroutineScope(Dispatchers.Main).launch {
                        val bm = async { Utils.converterURItoBitmap(context,imageUri) }
                        val rotatedBM = async { Utils.checkImageOrientation(bm.await(),file.absolutePath) }
                        val bmEncoded = async { Utils.converterBitmapToString(rotatedBM.await()) }
                        imageData.value = bmEncoded.await()
                        viewModel.addPayment()

                        Toast.makeText(context,"Payment Saved Successfully",Toast.LENGTH_SHORT).show()
                        navController.navigate(
                            "saved_payments_screen"
                        )

                    }
                }
            }
        })

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
                cameraLauncher.launch(imageUri)
            }else{
                Toast.makeText(context,"Permission Denied.",Toast.LENGTH_SHORT).show()
            }
        })






    Column (modifier = Modifier
        .padding(24.dp,0.dp,24.dp,16.dp)) {
        Row (modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){
            Checkbox(
                modifier = Modifier
                    .scale(1.5f),
                colors = CheckboxDefaults.colors(
                    checkmarkColor = Color.White,
                    checkedColor = OrangeCustom,
                    uncheckedColor = CheckboxLightGray
                ),
                checked = isTakePhotoChecked.value,
                onCheckedChange = { isChecked ->
                    isTakePhotoChecked.value = isChecked
                }
            )
            Text(
                modifier = Modifier.clickable {
                    isTakePhotoChecked.value = !isTakePhotoChecked.value
                },
                text = "Take photo of receipt",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Box (
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(listOf(OrangeCustom, DarkOrangeCustom)),
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .height(48.dp)
        ){
            Button(modifier = Modifier
                .fillMaxSize()
                .testTag(TestTags.SavePaymentButton),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
                , onClick = {
                    if(!viewModel.amountValidation(amountValue.value) ||
                        !viewModel.tipValidation(tipValue.value)){
                        amountErrorVisible.value = !viewModel.amountValidation(amountValue.value)
                        tipErrorVisible.value = !viewModel.tipValidation(tipValue.value)
                    }else{
                        viewModel.amountErrorVisible.value = false
                        viewModel.tipErrorVisible.value = false
                        if(!isTakePhotoChecked.value){
                            CoroutineScope(Dispatchers.Main).launch {
                                launch { viewModel.addPayment() }.join()
                                Toast.makeText(context,"Payment Saved Successfully",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            if(Utils.checkCameraPermission(context)){
                                cameraLauncher.launch(imageUri)
                            }else{
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }

                }) {
                Text(
                    text = "Save Payment",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}
