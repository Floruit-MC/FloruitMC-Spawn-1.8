
---

# 🤭 SpawnPlugin – Teleporte com Estilo e Controle

O **SpawnPlugin** é uma solução leve, moderna e altamente personalizável para comandos de `/spawn` em servidores Minecraft. Ele oferece **cooldown inteligente**, **fila de execução otimizada**, **teleporte em lote (batching)** e suporte a múltiplos spawns – incluindo fallback. Perfeito para servidores que buscam desempenho, segurança e integração fluida com outros sistemas.

---

## ⚙️ Funcionalidades Principais

| Recurso                                    | Status                                   |
| ------------------------------------------ | ---------------------------------------- |
| 🧊 Cooldown do `/spawn`                    | Totalmente configurável (`cooldown.yml`) |
| ⏳ Teleporte em lote (batch por tick)       | Personalizável (`batch.yml`)             |
| 🔄 Reload das configurações em tempo real  | Suportado                                |
| 📊 Métricas de desempenho                  | Ativáveis (`metrics.yml`)                |
| 🧠 Eventos customizados para integração    | Disponíveis                              |
| 🤭 Suporte a spawn principal e de fallback | Incluído                                 |
| 🥵 Fila de teleporte com prioridade        | Otimizada e automática                   |

---

## 🛡️ Sistema de Segurança e Controle

| Proteção                                     | Descrição                       |
| -------------------------------------------- | ------------------------------- |
| 📦 Cancelamento por outros plugins           | Permitido via eventos           |
| ❌ Prevenção de múltiplos teleportes seguidos | Gerenciado por fila             |
| 📉 Redução de carga do servidor              | Via batching automático         |
| ✅ Verificação de permissões                  | Integrada com sistemas externos |

---

## 💬 Comandos Disponíveis

### 🎮 Para Jogadores

| Comando           | Descrição                                    | Permissão                 |
| ----------------- | -------------------------------------------- | ------------------------- |
| `/spawn`          | Teleporta para o spawn principal             | *Acesso livre*            |
| `/spawn fallback` | Vai para o spawn de fallback, se configurado | `spawn.teleport.fallback` |
| `/spawn fila`     | Exibe sua posição atual na fila de teleporte | *Acesso livre*            |

### 🛠️ Para Administradores

| Comando                | Descrição                           | Permissão                                          |                |
| ---------------------- | ----------------------------------- | -------------------------------------------------- | -------------- |
| \`/setspawn \<spawn    | fallback>\`                         | Define o local atual como spawn principal ou extra | `spawn.set`    |
| \`/deletespawn \<spawn | fallback>\`                         | Remove um spawn registrado                         | `spawn.delete` |
| `/listspawn`           | Lista todos os spawns registrados   | `spawn.list`                                       |                |
| `/spawnconfig`         | Recarrega as configurações          | `spawn.reload`                                     |                |
| `/spawnconfig info`    | Exibe status do batching e métricas | `spawn.batchinfo`                                  |                |

---

## 🧾 Arquivos de Configuração

### 📄 `cooldown.yml`

Define o tempo de espera entre usos do comando `/spawn`:

```yaml
spawn:
  cooldown-seconds: 10
```

---

### 📄 `batch.yml`

Controla quantos jogadores podem ser teleportados por tick:

```yaml
max-teleports-per-tick: 5
```

---

### 📄 `metrics.yml`

Ativa ou desativa a coleta de dados internos:

```yaml
performance-metrics-enabled: true
```

---

### 📄 `messages.yml`

Personalize todas as mensagens do plugin com placeholders dinâmicos:

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

## ✅ Requisitos e Recomendações

* 🔐 Compatível com sistemas de permissão como **LuckPerms**
* 🧩 Pode ser integrado com outros plugins via eventos
* 🧪 Compatível com servidores **Paper** 1.16+ ou superiores
* 🎮 Ideal para servidores **Survival**, **Lobby**, **MiniGames**, **RPG** e **Roleplay**
