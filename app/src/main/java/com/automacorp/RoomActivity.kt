package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.model.RoomViewModel
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val viewModel: RoomViewModel by viewModels()
        viewModel.room = RoomService.findByNameOrId(param)

        val onRoomSave: () -> Unit = {
            if(viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                RoomService.updateRoom(roomDto.id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG).show()
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack) },
                    floatingActionButton = { RoomUpdateButton(onRoomSave) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (viewModel.room != null) {
                        RoomDetail(viewModel, Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomDetail(model: RoomViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.act_room_name),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = model.room?.name ?: "",
            onValueChange = { model.room?.name = it },
            label = { Text(text = stringResource(R.string.act_room_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.act_room_current_temperature, model.room?.currentTemperature ?: "N/A"),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = model.room?.currentTemperature.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = stringResource(R.string.act_room_target_temperature, model.room?.currentTemperature ?: "N/A"),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Slider(
            value = model.room?.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = {
                model.room = model.room?.copy(targetTemperature = it.toDouble())
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f
        )

        Text(
            text = (round((model.room?.targetTemperature ?: 18.0) * 10) / 10).toString() + "°C",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.act_room_none),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_room_save)) }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun RoomDetailPreview() {
//    AutomacorpTheme {
//        RoomDetail(
//            roomDto = RoomDto(
//                id = 1,
//                name = "Room 1",
//                currentTemperature = 22.0,
//                targetTemperature = 23.0,
//                windows = emptyList()
//            ),
//            modifier = Modifier
//        )
//    }
//}

