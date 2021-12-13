package me.monster.viewcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gyf.immersionbar.ImmersionBar
import me.monster.viewcollection.page.ViewRootActivity
import me.monster.viewcollection.ui.theme.ViewCollectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersionBar.with(this)
            .fullScreen(true)
            .statusBarDarkFont(true)
            .navigationBarDarkIcon(true)
            .transparentStatusBar()
            .transparentNavigationBar()
            .init()

        setContent {
            ViewCollectionTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Entrance({
                        ViewRootActivity.start(this)
                    }, {

                    })
                }
            }
        }
    }
}

@Composable
fun Entrance(androidClick: () -> Unit, composeClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { androidClick() }) {
            Text(text = "Android View")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { composeClick() }) {
            Text(text = "Jetpack Compose")
        }
    }
}