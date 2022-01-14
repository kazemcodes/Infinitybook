package ir.kazemcodes.infinity.presentation.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import ir.kazemcodes.infinity.R
import ir.kazemcodes.infinity.sources.Extensions
import ir.kazemcodes.infinity.presentation.book_detail.Constants.DEFAULT_ELEVATION
import ir.kazemcodes.infinity.presentation.home.BrowserScreenKey
import org.kodein.di.compose.rememberInstance


@Composable
fun ExtensionScreen(modifier: Modifier = Modifier) {
    val backstack = LocalBackstack.current
    val extensions: Extensions by rememberInstance<Extensions>()

    val sources = extensions.getSources()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Extensions",
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = DEFAULT_ELEVATION,
            )
        }
    ) {
        LazyColumn {
            items(sources.size) { index ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(30.dp)
                    .clickable {
                        backstack.goTo(BrowserScreenKey(sources[index].name))
                    },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(sources[index].name)
                    if (sources[index].supportsMostPopular) {
                        Text(stringResource(R.string.popular_book),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.subtitle2,
                            modifier = Modifier.clickable {
                                backstack.goTo(BrowserScreenKey(sourceName = sources[index].name,
                                    isLatestUpdateMode = false))
                            })
                    }
                }

            }
        }


    }
}