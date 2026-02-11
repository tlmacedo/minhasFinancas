package br.com.tlmacedo.minhasfinancas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Mapa de ícones disponíveis para contas
object ContaIcons {
    val icons = mapOf(
        "account_balance" to Icons.Filled.AccountBalance,
        "account_balance_wallet" to Icons.Filled.AccountBalanceWallet,
        "savings" to Icons.Filled.Savings,
        "wallet" to Icons.Filled.Wallet,
        "credit_card" to Icons.Filled.CreditCard,
        "payments" to Icons.Filled.Payments,
        "paid" to Icons.Filled.Paid,
        "currency_exchange" to Icons.Filled.CurrencyExchange,
        "trending_up" to Icons.Filled.TrendingUp,
        "attach_money" to Icons.Filled.AttachMoney,
        "money" to Icons.Filled.Money,
        "local_atm" to Icons.Filled.LocalAtm,
        "store" to Icons.Filled.Store,
        "shopping_bag" to Icons.Filled.ShoppingBag,
        "home" to Icons.Filled.Home,
        "apartment" to Icons.Filled.Apartment,
        "work" to Icons.Filled.Work,
        "business_center" to Icons.Filled.BusinessCenter,
        "pie_chart" to Icons.Filled.PieChart,
        "bar_chart" to Icons.Filled.BarChart,
        "diamond" to Icons.Filled.Diamond,
        "stars" to Icons.Filled.Stars,
        "favorite" to Icons.Filled.Favorite,
        "shield" to Icons.Filled.Shield,
        "lock" to Icons.Filled.Lock,
        "vpn_key" to Icons.Filled.VpnKey,
        "smartphone" to Icons.Filled.Smartphone,
        "computer" to Icons.Filled.Computer,
        "directions_car" to Icons.Filled.DirectionsCar,
        "flight" to Icons.Filled.Flight
    )
    
    fun getIcon(name: String?): ImageVector {
        return icons[name] ?: Icons.Filled.AccountBalanceWallet
    }
    
    fun getIconName(icon: ImageVector): String {
        return icons.entries.find { it.value == icon }?.key ?: "account_balance_wallet"
    }
}

@Composable
fun IconSelector(
    selectedIcon: String?,
    selectedColor: Color,
    onIconSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val currentIcon = ContaIcons.getIcon(selectedIcon)
    
    Column(modifier = modifier) {
        Text(
            text = "Ícone da Conta",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(selectedColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentIcon,
                        contentDescription = null,
                        tint = selectedColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Toque para alterar o ícone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    if (showDialog) {
        IconPickerDialog(
            selectedIcon = selectedIcon,
            selectedColor = selectedColor,
            onIconSelect = { 
                onIconSelect(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun IconPickerDialog(
    selectedIcon: String?,
    selectedColor: Color,
    onIconSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Escolha um Ícone",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(ContaIcons.icons.entries.toList()) { (name, icon) ->
                        val isSelected = name == selectedIcon
                        
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) selectedColor.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.dp, 
                                        selectedColor, 
                                        RoundedCornerShape(8.dp)
                                    ) else Modifier
                                )
                                .clickable { onIconSelect(name) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                tint = if (isSelected) selectedColor 
                                       else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
