package com.scrollstop.app.ui.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import com.scrollstop.app.ui.theme.AccentGold
import com.scrollstop.app.ui.theme.BackgroundDark
import com.scrollstop.app.ui.theme.StopRed
import com.scrollstop.app.ui.theme.SurfaceBorderDark
import com.scrollstop.app.ui.theme.SurfaceDark
import com.scrollstop.app.ui.theme.SurfaceVariantDark
import com.scrollstop.app.ui.theme.TextMuted
import com.scrollstop.app.ui.theme.TextPrimary
import com.scrollstop.app.ui.theme.TextSecondary
import com.scrollstop.app.ui.theme.WorkGreen
import java.io.File

@Composable
fun MotivationGalleryScreen(
    viewModel: MotivationGalleryViewModel
) {
    val items by viewModel.motivationalItems.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MOTIVATION GALLERY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PERSONAL WEAPONS AGAINST TEMPTATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StopRed,
                    letterSpacing = 1.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = StopRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "ADD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Selector Card: Random vs Fixed
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp)),
            color = SurfaceDark,
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Random Selection Mode",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (prefs.useRandomMotivation) "Picks a random motivation on each reminder" else "Always shows your active selected motivation",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Switch(
                    checked = prefs.useRandomMotivation,
                    onCheckedChange = { viewModel.toggleRandomMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextPrimary,
                        checkedTrackColor = StopRed,
                        uncheckedTrackColor = SurfaceVariantDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(items, key = { it.id }) { item ->
                MotivationItemCard(
                    item = item,
                    isRandomMode = prefs.useRandomMotivation,
                    onSetActive = { viewModel.setActiveItem(item.id) },
                    onDelete = { viewModel.deleteItem(item) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddMotivationDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { uri, quote, author ->
                viewModel.addCustomMotivation(uri, quote, author)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MotivationItemCard(
    item: MotivationalItemEntity,
    isRandomMode: Boolean,
    onSetActive: () -> Unit,
    onDelete: () -> Unit
) {
    val localFile = item.imagePath?.let { File(it) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (item.isActive && !isRandomMode) StopRed else SurfaceBorderDark,
                RoundedCornerShape(16.dp)
            ),
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Tag / Active Indicator + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isActive && !isRandomMode) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StopRed)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "ACTIVE INTERVENTION",
                            color = TextPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Text(
                        text = if (item.isUserAdded) "MY MOTIVATION" else "BUILT-IN WARRIOR",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (item.isUserAdded) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Image Preview if available
            if (localFile != null && localFile.exists()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = localFile,
                        contentDescription = "Motivation Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quote Text
            Text(
                text = item.quote,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "— ${item.author}",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            if (!item.isActive && !isRandomMode) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onSetActive,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "SET AS ACTIVE INTERVENTION", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AddMotivationDialog(
    onDismiss: () -> Unit,
    onConfirm: (imageUri: Uri?, quote: String, author: String) -> Unit
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var quoteText by remember { mutableStateOf("") }
    var authorText by remember { mutableStateOf("") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(text = "Add Motivational Weapon", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Photo Picker Button / Preview
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                        .clickable { photoPicker.launch("image/*") },
                    color = SurfaceVariantDark
                ) {
                    if (selectedUri != null) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = TextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Tap to pick photo from gallery", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = quoteText,
                    onValueChange = { quoteText = it },
                    label = { Text("Motivational Reality Check") },
                    placeholder = { Text("What goals will you fail if you keep scrolling?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = StopRed,
                        unfocusedBorderColor = SurfaceBorderDark
                    )
                )

                OutlinedTextField(
                    value = authorText,
                    onValueChange = { authorText = it },
                    label = { Text("Tag / Author") },
                    placeholder = { Text("e.g. My Future, Future Founder") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = StopRed,
                        unfocusedBorderColor = SurfaceBorderDark
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedUri, quoteText, authorText) },
                colors = ButtonDefaults.buttonColors(containerColor = StopRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "SAVE TO PHONE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark)
            ) {
                Text(text = "CANCEL", color = TextMuted)
            }
        }
    )
}
