# 🤭 Spawn - Teleporte com Estilo e Controle

O **SpawnPlugin** é um plugin leve e eficiente focado em oferecer um sistema completo de **teleporte para spawn** com **cooldown**, **fila de execução (batch)** e **suporte a spawns personalizados**, incluindo fallback. Ideal para servidores que buscam controle, desempenho e integração com outros sistemas.

---

## ⚙️ Configurações Gerais

| Configuração                                 | Estado        |
|---------------------------------------------|---------------|
| 🧊 Cooldown global do comando `/spawn`       | Personalizável (`cooldown.yml`) |
| ⏳ Teleporte em lote por tick (batching)     | Personalizável (`batch.yml`) |
| 🔄 Reload dinâmico de configurações          | Suportado     |
| 📊 Métricas de performance                   | Ativáveis (`metrics.yml`) |
| 🧠 Eventos customizados                      | Disponíveis para integração |
| 🤭 Spawn principal e fallback                | Suporte total |
| 🥵 Fila de execução de teleportes            | Otimizada por prioridade |

---

## 🛡️ Sistema de Proteção no Teleporte

| Recurso                                      | Estado        |
|---------------------------------------------|---------------|
| 📦 Cancelamento por outros plugins           | Permitido via evento |
| ❌ Execução de múltiplos teleportes          | Prevenido por fila inteligente |
| 📉 Carga reduzida por batching               | Ativado automaticamente |
| ✅ Verificações de permissão integradas      | Sim |

---

## 💬 Comandos Disponíveis

### 🎮 Comandos Gerais

| Comando                   | Função                                                        | Permissão                    |
|---------------------------|---------------------------------------------------------------|------------------------------|
| `/spawn`                  | Teleporta o jogador para o spawn principal                   | -                            |
| `/spawn fallback`         | Teleporta para o spawn de fallback                           | `spawn.teleport.fallback`    |
| `/spawn fila`             | Mostra sua posição na fila de teleporte                      | -                            |

### 🛠️ Comandos Administrativos

| Comando                   | Função                                                        | Permissão                    |
|---------------------------|---------------------------------------------------------------|------------------------------|
| `/setspawn <spawn|fallback>` | Define a localização atual como spawn                       | `spawn.set`                  |
| `/deletespawn <spawn|fallback>` | Remove o spawn definido                                  | `spawn.delete`               |
| `/listspawn`              | Lista os spawns disponíveis                                  | `spawn.list`                 |
| `/spawnconfig`            | Recarrega as configurações do plugin                         | `spawn.reload`               |
| `/spawnconfig info`       | Mostra dados do batching e métricas                          | `spawn.batchinfo`            |

---

## 📟 Arquivos de Configuração

### 📄 `cooldown.yml`

```yaml
spawn:
  cooldown-seconds: 10
```

Define o tempo de espera entre usos do comando `/spawn`.

---

### 📄 `batch.yml`

```yaml
max-teleports-per-tick: 5
```

Controla quantos jogadores podem ser teleportados por tick, evitando lag.

---

### 📄 `metrics.yml`

```yaml
performance-metrics-enabled: true
```

Ativa/desativa a coleta de métricas internas de uso.

---

### 📄 `messages.yml`

Mensagens personalizáveis com placeholders. Exemplo:

```yaml
spawn:
  on_cooldown: "&cEspere {time} segundos para usar novamente."
  added_to_queue: "&aVocê está na fila! Posição: {position}"
  already_in_queue: "&eVocê já está na fila. Posição: {position}"
  teleport_cancelled: "&cSeu teleporte foi cancelado."
  no_spawns_set: "&cNenhum spawn definido!"
  queue_position: "&eSua posição na fila: {position}"
config:
  reload_success: "&aConfigurações recarregadas com sucesso! Batch: {maxTeleports}, Métricas: {metricsStatus}"
```

---

## ✅ Observações

- Recomenda-se o uso com plugins de permissões como **LuckPerms**
- O plugin é compatível com servidores Paper 1.16+ ou superiores
- Ideal para servidores **Survival**, **Lobby**, **MiniGames** e **Roleplay**

