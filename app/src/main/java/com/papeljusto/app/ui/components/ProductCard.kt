package com.papeljusto.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papeljusto.app.domain.model.ConfidenceLevel
import com.papeljusto.app.domain.model.Product
import com.papeljusto.app.ui.theme.Amarillo
import com.papeljusto.app.ui.theme.Dorado
import com.papeljusto.app.ui.theme.Rojo
import com.papeljusto.app.ui.theme.Verde

@Composable
fun ProductCard(
    product: Product,
    posicion: Int,
    esMejorCompra: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
)
{
    val colorSemaforo = when
    {
        posicion == 1 -> Verde
        posicion == 2 -> Amarillo
        else -> Rojo
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    )
    {
        Column(modifier = Modifier.padding(20.dp))
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            )
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(colorSemaforo)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = product.marca,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    if (esMejorCompra)
                    {
                        Text(text = "Mejor compra", fontSize = 13.sp, color = Dorado, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp))
                    {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            )
            {
                Column()
                {
                    Text(
                        text = "$ %.0f".format(product.precio),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${product.cantidadRollos} rollos · ${product.plyType.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Column(horizontalAlignment = Alignment.End)
                {
                    Text(
                        text = "$ %.0f /m²".format(product.costoPorM2),
                        style = MaterialTheme.typography.titleMedium,
                        color = colorSemaforo,
                        fontWeight = FontWeight.Bold
                    )
                    ConfianzaChip(confianza = product.confianza)
                }
            }
        }
    }
}

@Composable
private fun ConfianzaChip(confianza: ConfidenceLevel)
{
    val (color, bgColor) = when (confianza)
    {
        ConfidenceLevel.ALTA -> Pair(Verde, Verde.copy(alpha = 0.12f))
        ConfidenceLevel.MEDIA -> Pair(Amarillo, Amarillo.copy(alpha = 0.12f))
        ConfidenceLevel.BAJA -> Pair(Rojo, Rojo.copy(alpha = 0.12f))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
    {
        Text(
            text = "Precisión ${confianza.label.lowercase()}",
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
