package com.atlas.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import java.text.NumberFormat
import java.util.Locale

private val Green = Color(0xFFE83E8C)
private val SoftGreen = Color(0xFFFFE8F2)
private val Red = Color(0xFFC6285C)
private val Bg = Color(0xFFFFF6FA)

data class FinanceState(
    var salary: Double = 5000.0,
    var fixed: Double = 2650.0,
    var variable: Double = 1102.0,
    var reserve: Double = 300.0,
    var cardUsed: Double = 1850.0,
    var cardLimit: Double = 3000.0
) {
    val available get() = salary - fixed - variable
}

fun brl(v: Double): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(v)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Green,
                    background = Bg,
                    surface = Color.White,
                    error = Red
                )
            ) { FinanceApp() }
        }
    }
}

@Composable
fun FinanceApp() {
    val nav = rememberNavController()
    val state = remember { mutableStateOf(FinanceState()) }

    Scaffold(
        bottomBar = { BottomBar(nav) },
        containerColor = Bg
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = "home",
            modifier = Modifier.padding(pad)
        ) {
            composable("home") { HomeScreen(nav, state.value) }
            composable("transactions") { TransactionsScreen() }
            composable("cards") { CardsScreen(state.value) }
            composable("profile") { ProfileScreen() }
            composable("buy") { CanIBuyScreen() }
            composable("expense") { NewExpenseScreen(state) }
            composable("budget") { BudgetScreen(state.value) }
            composable("goals") { GoalsScreen() }
            composable("reports") { ReportsScreen(state.value) }
            composable("alerts") { AlertsScreen() }
        }
    }
}

@Composable
fun BottomBar(nav: NavHostController) {
    NavigationBar(containerColor = Color.White) {
        val entries = listOf(
            Triple("home", Icons.Default.Home, "Início"),
            Triple("transactions", Icons.Default.ReceiptLong, "Transações"),
            Triple("expense", Icons.Default.AddCircle, "Adicionar"),
            Triple("cards", Icons.Default.CreditCard, "Cartões"),
            Triple("profile", Icons.Default.Person, "Perfil")
        )
        entries.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = false,
                onClick = { nav.navigate(route) { launchSingleTop = true } },
                icon = { Icon(icon, label) },
                label = { Text(label, fontSize = 10.sp) }
            )
        }
    }
}

@Composable
fun ScreenTitle(title: String) {
    Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp))
}

@Composable
fun FinanceCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

@Composable
fun HomeScreen(nav: NavHostController, s: FinanceState) {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            Text("Bom dia 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Seu mês • Agosto", color = Color.Gray)
            Spacer(Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftGreen),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Situação do mês", fontWeight = FontWeight.SemiBold)
                    Text("No controle ✓", color = Green)
                    Spacer(Modifier.height(14.dp))
                    Text("Disponível para gastar", color = Color.DarkGray)
                    Text(brl(s.available), fontSize = 32.sp, color = Green, fontWeight = FontWeight.Bold)
                    Text("Limite sugerido por dia: ${brl((s.available.coerceAtLeast(0.0))/30)}")
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("Próximos vencimentos", fontWeight = FontWeight.Bold)
        }
        items(listOf(
            "Aluguel • 10 SET" to 1200.0,
            "Internet • 12 SET" to 120.0,
            "Cartão de crédito • 20 SET" to 850.0,
            "Conta de luz • 25 SET" to 210.0
        )) { (name, value) ->
            FinanceCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name)
                    Text(brl(value), fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Text("Ações rápidas", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Quick("Nova despesa", Icons.Default.Add) { nav.navigate("expense") }
                Quick("Posso comprar?", Icons.Default.ShoppingBag) { nav.navigate("buy") }
                Quick("Relatórios", Icons.Default.BarChart) { nav.navigate("reports") }
                Quick("Metas", Icons.Default.Flag) { nav.navigate("goals") }
            }
            Button(onClick = { nav.navigate("alerts") }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(8.dp)); Text("Ver avisos")
            }
        }
    }
}

@Composable
fun Quick(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, action: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(78.dp)) {
        FilledTonalIconButton(onClick = action) { Icon(icon, label) }
        Text(label, fontSize = 10.sp)
    }
}

@Composable
fun CanIBuyScreen() {
    var description by remember { mutableStateOf("") }
    var valueText by remember { mutableStateOf("") }
    var installments by remember { mutableStateOf("1") }
    var result by remember { mutableStateOf<String?>(null) }

    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            ScreenTitle("Posso comprar?")
            Text("Simule antes de gastar e veja o impacto no seu orçamento.")
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(description, { description = it }, label = { Text("O que deseja comprar?") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(valueText, { valueText = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                label = { Text("Valor total") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            OutlinedTextField(installments, { installments = it.filter(Char::isDigit) },
                label = { Text("Número de parcelas") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            Button(
                onClick = {
                    val v = valueText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val p = installments.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    val monthly = v / p
                    result = when {
                        monthly <= 100 -> "🟢 Cabe no orçamento. Parcela estimada: ${brl(monthly)}."
                        monthly <= 250 -> "🟡 Atenção. A compra compromete ${brl(monthly)} por mês."
                        else -> "🔴 Melhor esperar. A parcela de ${brl(monthly)} pode comprometer demais o orçamento."
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
            ) { Text("Verificar se posso comprar") }
            result?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (it.startsWith("🔴")) Color(0xFFFFE4ED) else SoftGreen),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) { Text(it, Modifier.padding(18.dp), fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
fun NewExpenseScreen(state: MutableState<FinanceState>) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Alimentação") }
    var saved by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            ScreenTitle("Nova despesa")
            OutlinedTextField(amount, { amount = it }, label = { Text("Valor em R$") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(category, { category = it }, label = { Text("Categoria") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            Button(onClick = {
                val v = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
                state.value = state.value.copy(variable = state.value.variable + v)
                saved = true
            }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) { Text("Salvar despesa") }
            if (saved) Text("Despesa registrada ✓", color = Green, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
fun TransactionsScreen() {
    val tx = listOf(
        "Mercado" to -132.50, "Uber" to -28.90, "Salário" to 5000.0,
        "Alimentação" to -75.40, "Farmácia" to -42.90, "Streaming" to -29.90
    )
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item { ScreenTitle("Transações") }
        items(tx) { (n, v) ->
            FinanceCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(n, fontWeight = FontWeight.Medium)
                    Text((if (v > 0) "+" else "") + brl(v), color = if (v > 0) Green else Red)
                }
            }
        }
    }
}

@Composable
fun CardsScreen(s: FinanceState) {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            ScreenTitle("Cartões e contas")
            FinanceCard {
                Text("Cartão de crédito", fontWeight = FontWeight.Bold)
                Text("Limite total: ${brl(s.cardLimit)}")
                Text("Utilizado: ${brl(s.cardUsed)}")
                LinearProgressIndicator(progress = { (s.cardUsed / s.cardLimit).toFloat().coerceIn(0f,1f) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                Text("Disponível: ${brl(s.cardLimit - s.cardUsed)}", color = Green)
                Text("Fatura atual: ${brl(850.0)} • vence dia 20")
            }
            FinanceCard {
                Text("Conta bancária", fontWeight = FontWeight.Bold)
                Text("Saldo: ${brl(1326.40)}")
            }
            TextButton(onClick = {}) { Text("Parcelas futuras") }
        }
    }
}

@Composable
fun BudgetScreen(s: FinanceState) {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            ScreenTitle("Orçamento")
            FinanceCard {
                Text("Receita", fontWeight = FontWeight.Bold); Text(brl(s.salary), color = Green)
                Text("Contas fixas"); Text(brl(s.fixed))
                Text("Gastos variáveis"); Text(brl(s.variable))
                Text("Reserva programada"); Text(brl(s.reserve))
                Divider(Modifier.padding(vertical = 10.dp))
                Text("Disponível: ${brl(s.available)}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GoalsScreen() {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item { ScreenTitle("Metas") }
        items(listOf("Reserva de emergência" to .41f, "Viagem" to .37f, "Trocar de celular" to .12f)) { (n,p) ->
            FinanceCard {
                Text(n, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(progress = { p }, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                Text("${(p*100).toInt()}% concluído", color = Green)
            }
        }
    }
}

@Composable
fun ReportsScreen(s: FinanceState) {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            ScreenTitle("Relatórios")
            FinanceCard {
                Text("Resumo do mês", fontWeight = FontWeight.Bold)
                Text("Entradas: ${brl(s.salary)}", color = Green)
                Text("Despesas: ${brl(s.fixed + s.variable)}", color = Red)
                Text("Moradia • 53%")
                Text("Transportes • 13%")
                Text("Alimentação • 11%")
                Text("Lazer • 6%")
                Text("Outros • 17%")
            }
        }
    }
}

@Composable
fun AlertsScreen() {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item { ScreenTitle("Avisos") }
        items(listOf(
            "⚠️ Você já ultrapassou o limite de gastos desta semana.",
            "🏠 Aluguel vence em 6 dias.",
            "💳 Sua fatura do cartão está próxima do limite.",
            "✅ Você já guardou dinheiro para sua meta este mês."
        )) { msg -> FinanceCard { Text(msg) } }
    }
}

@Composable
fun ProfileScreen() {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            ScreenTitle("Perfil")
            FinanceCard {
                Text("Minha conta", fontWeight = FontWeight.Bold)
                Text("Dados pessoais")
                Text("Preferências")
                Text("Notificações")
                Text("Segurança")
                Text("Backup e exportação")
            }
        }
    }
}
