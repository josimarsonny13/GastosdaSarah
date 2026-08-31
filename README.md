# Gastos da Sarah

Aplicativo Android em Kotlin + Jetpack Compose criado como protótipo de um assistente financeiro preventivo.

## Funções incluídas
- Tela inicial com saldo disponível e próximos vencimentos
- Registro de despesas
- Lista de transações
- "Posso comprar?" com simulação de parcelas
- Cartões e contas
- Orçamento
- Metas
- Relatórios
- Avisos
- Perfil

## Abrir no Android Studio
1. Extraia o ZIP.
2. Abra a pasta `Gastos_da_Sarah` no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Use um emulador ou celular Android com depuração USB/Wi-Fi.
5. Clique em **Run**.

## GitHub
Crie um repositório vazio e envie todos os arquivos desta pasta.

> Esta é uma primeira versão funcional/protótipo. Os dados ficam em memória e são reiniciados ao fechar o app. Uma próxima versão pode usar Room/DataStore para armazenamento permanente, notificações, autenticação e gráficos avançados.


## Tema visual
Interface atualizada com tons de rosa mesclados, rosa claro e fundo blush.


## Gerar APK pelo GitHub Actions

Esta versão já contém `.github/workflows/gerar-apk.yml`.

1. Apague do repositório os arquivos do projeto antigo, especialmente workflows que mencionem `AbasteceCasa`, `Flutter` ou `DOC-20260824-WA0049.zip`.
2. Envie o conteúdo desta pasta para a raiz do repositório.
3. Confirme que `settings.gradle.kts`, `build.gradle.kts`, `app/` e `.github/` aparecem diretamente na raiz.
4. Abra a aba **Actions**.
5. Selecione **Gerar APK Gastos da Sarah**.
6. Execute **Run workflow**.
7. Quando terminar, abra a execução e baixe o artifact **Gastos-da-Sarah-APK**.

O workflow compila diretamente o projeto Kotlin/Jetpack Compose; ele não procura ZIP antigo e não instala Flutter.
