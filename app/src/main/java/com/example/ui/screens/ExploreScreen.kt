package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.CreatorCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    creators: List<CreatorProfile>,
    searchQuery: String,
    selectedCategory: CreatorCategory?,
    selectedLanguageFilter: String?,
    selectedSubscriberRange: SubscriberRange?,
    currentLanguage: AppLanguage,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (CreatorCategory?) -> Unit,
    onLanguageFilterSelect: (String?) -> Unit,
    onSubscriberRangeSelect: (SubscriberRange?) -> Unit,
    onResetFilters: () -> Unit,
    onCreatorClick: (CreatorProfile) -> Unit,
    onCollabClick: (CreatorProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFilterSheet by remember { mutableStateOf(false) }

    val hasActiveFilters = searchQuery.isNotBlank() ||
            selectedCategory != null ||
            selectedLanguageFilter != null ||
            selectedSubscriberRange != null

    val languageOptions = listOf("All", "Hindi", "English", "Hinglish")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Search & Filter Action Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("explore_search_input"),
                        placeholder = {
                            Text(
                                if (currentLanguage == AppLanguage.HINDI)
                                    "क्रिएटर या टॉपिक खोजें..."
                                else
                                    "Search creators, topics...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = PurplePrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = NavySurface,
                            unfocusedContainerColor = NavySurface
                        ),
                        singleLine = true
                    )

                    // Filter Modal Toggle Button
                    IconButton(
                        onClick = { showFilterSheet = !showFilterSheet },
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                if (hasActiveFilters) PurplePrimary else NavySurface,
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, if (hasActiveFilters) PurplePrimary else NavyBorder, RoundedCornerShape(12.dp))
                            .testTag("toggle_filters_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = if (hasActiveFilters) Color.White else ElectricCyan
                        )
                    }
                }

                // Category Chips Row
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onCategorySelect(null) },
                            label = {
                                Text(if (currentLanguage == AppLanguage.HINDI) "सभी" else "All")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = NavySurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (selectedCategory == null) PurplePrimary else NavyBorder,
                                enabled = true,
                                selected = selectedCategory == null
                            )
                        )
                    }
                    items(CreatorCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(if (isSelected) null else cat) },
                            label = {
                                Text(cat.localizedTitle(currentLanguage))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cat.color,
                                selectedLabelColor = Color.White,
                                containerColor = NavySurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSelected) cat.color else NavyBorder,
                                enabled = true,
                                selected = isSelected
                            )
                        )
                    }
                }

                // Quick Secondary Filter Bar (Language & Sub Range)
                if (showFilterSheet) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NavySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Language Filter Row
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "भाषा फ़िल्टर:" else "Language Filter:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                languageOptions.forEach { lang ->
                                    val isSelected = (lang == "All" && selectedLanguageFilter == null) ||
                                            selectedLanguageFilter.equals(lang, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) ElectricCyan else NavySurfaceVariant,
                                        modifier = Modifier
                                            .clickable {
                                                onLanguageFilterSelect(if (lang == "All") null else lang)
                                            }
                                    ) {
                                        Text(
                                            text = lang,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (isSelected) Color.Black else TextSecondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Subscriber Range Filter Row
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "सब्सक्राइबर रेंज:" else "Subscriber Range:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                item {
                                    val isSelected = selectedSubscriberRange == null
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) PurplePrimary else NavySurfaceVariant,
                                        modifier = Modifier.clickable { onSubscriberRangeSelect(null) }
                                    ) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) "सभी रेंज" else "Any Subs",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (isSelected) Color.White else TextSecondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                                items(SubscriberRange.values()) { range ->
                                    val isSelected = selectedSubscriberRange == range
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) PurplePrimary else NavySurfaceVariant,
                                        modifier = Modifier.clickable { onSubscriberRangeSelect(if (isSelected) null else range) }
                                    ) {
                                        Text(
                                            text = range.localizedLabel(currentLanguage),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (isSelected) Color.White else TextSecondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            if (hasActiveFilters) {
                                Spacer(modifier = Modifier.height(10.dp))
                                TextButton(
                                    onClick = onResetFilters,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = RoseError
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर हटाएं" else "Reset All Filters",
                                        color = RoseError,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }

                // Results Count & Active Filter Indicator
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "${creators.size} क्रिएटर उपलब्ध"
                        else
                            "Showing ${creators.size} creators",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )

                    if (hasActiveFilters) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PurplePrimary.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { onResetFilters() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = PurpleSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर रीसेट" else "Clear filters",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PurpleSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Empty State Handler
        if (creators.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = PurpleSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "कोई क्रिएटर नहीं मिला"
                            else
                                "No Creators Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "कृपया अपने फ़िल्टर या खोज शब्द बदलकर देखें।"
                            else
                                "Try broadening your category, language, or subscriber range filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Button(
                            onClick = onResetFilters,
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर हटाएं" else "Reset Filters")
                        }
                    }
                }
            }
        } else {
            // Creators List
            items(creators, key = { it.id }) { creator ->
                CreatorCard(
                    creator = creator,
                    currentLanguage = currentLanguage,
                    onCardClick = { onCreatorClick(creator) },
                    onCollabClick = { onCollabClick(creator) },
                    onOpenChannelUrl = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}
