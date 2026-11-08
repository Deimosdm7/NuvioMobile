# Nuvio Enhanced — Roadmap do fork

Este repositório é um fork comunitário e independente do Nuvio Mobile. A intenção é preservar os recursos existentes e acrescentar melhorias em etapas pequenas, testáveis e reversíveis.

## Princípios

- Preservar os recursos originais do Nuvio; não remover funções para criar uma experiência apenas de IPTV.
- Manter compatibilidade com a arquitetura Kotlin Multiplatform / Compose Multiplatform existente.
- Não embutir listas, credenciais, chaves privadas ou fontes de conteúdo de terceiros.
- Priorizar fontes configuradas pelo próprio usuário e serviços oficiais.
- Implementar cada recurso em commits separados, com testes e documentação.
- Respeitar a licença GPL-3.0 e manter os avisos de copyright aplicáveis.

## Escopo desejado

### 1. TV ao vivo
- Catálogo de canais e grupos para fontes M3U/M3U8 fornecidas pelo usuário.
- Favoritos, busca e guia de programação (EPG/XMLTV).
- Opções de reprodução e retomada quando suportadas pela fonte.
- Avaliar suporte a Xtream Codes e Stalker Portal antes de implementar, verificando segurança, compatibilidade e requisitos técnicos.

### 2. Filmes e séries
- Preservar os catálogos e detalhes já existentes.
- Melhorar navegação por temporadas e episódios.
- Exibir metadados, artes e trailers por integrações autorizadas/configuradas.
- Não hospedar nem fornecer conteúdo protegido.

### 3. Biblioteca e personalização
- Continuar assistindo, histórico e favoritos.
- Coleções personalizadas e organização da página inicial.
- Preferências de layout, temas e densidade dos cartões.
- Backup e restauração das preferências sem expor tokens ou senhas.

### 4. Player
- Melhorias de seleção de áudio e legendas.
- Ajuste de proporção, velocidade e controles.
- Reprodução externa e Picture-in-Picture quando disponíveis na plataforma.
- Testes de regressão para Android e demais alvos suportados.

### 5. Configurações e qualidade
- Gerenciamento claro das fontes e integrações.
- Diagnóstico de conectividade com dados sensíveis ocultados.
- Acessibilidade, desempenho e estabilidade.
- Builds de teste e notas de versão próprias do fork.

## Plano de execução

1. **Auditoria da base:** mapear telas, navegação, armazenamento, player, fontes e recursos já existentes para evitar duplicações.
2. **Identidade do fork:** definir nome, ícone, identificador de pacote e canais de atualização sem sobrescrever a identidade do app original.
3. **Primeira melhoria funcional:** escolher um módulo pequeno e implementar com testes.
4. **Integração gradual:** adicionar os módulos restantes em mudanças isoladas.
5. **Validação:** executar testes e build Android antes de publicar APKs.

## Implementação IPTV — primeira etapa

Foi adicionado um parser M3U/M3U8 independente de plataforma em `features/iptv/M3uPlaylistParser.kt`. Ele lê canais, URLs, `tvg-id`, `tvg-name`, `tvg-logo`, `group-title` e `#EXTGRP`, preservando atributos adicionais e avisando sobre entradas sem URL. Testes comuns cobrem metadados, nomes com vírgulas, BOM/CRLF, comentários e entradas incompletas. Também foi criada uma primeira tela IPTV: ela aceita conteúdo M3U colado, lista os canais identificados e encaminha o canal selecionado ao player interno já existente. Esta etapa ainda não faz download de listas por URL nem salva fontes entre reinicializações.

## Estado

- [x] Fork criado em `Deimosdm7/NuvioMobile`.
- [x] Roadmap inicial registrado.
- [x] Auditoria inicial da navegação, player e estrutura de módulos: a base já possui navegação por abas e player reutilizável.
- [x] Implementar a primeira peça funcional de IPTV: parser M3U/M3U8 multiplataforma com testes comuns.
- [x] Integrar uma aba IPTV inicial à navegação Compose, preservando Home, Search, Library e Settings.
- [ ] Ampliar a importação para URL/arquivo e validar a integração com diferentes formatos de stream.
- [ ] Implementar gerenciamento de listas e persistência local segura.
- [ ] Integrar EPG/XMLTV e, em etapas posteriores, Xtream Codes e Stalker Portal.
- [ ] Definir identidade visual e identificador próprios.
- [ ] Validar build Android e testes.

## Validação da primeira aba IPTV

A primeira integração visual está no branch de desenvolvimento. A validação de compilação Android ainda precisa ser concluída pelo workflow de CI antes de distribuir um APK.

## Nota

Este roadmap descreve objetivos, não afirma que esses recursos novos já estejam implementados. O código original continua sendo a base até que cada mudança seja desenvolvida e validada.
