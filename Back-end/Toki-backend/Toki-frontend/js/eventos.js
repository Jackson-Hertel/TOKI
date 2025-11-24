// ================================
// CONFIGURAÇÃO
// ================================
const API_BASE = "http://localhost:8080/toki/evento";

document.addEventListener("DOMContentLoaded", () => {
    carregarEventos();
    configurarFormulario();
});


// ================================
// BUSCAR EVENTOS
// ================================
async function carregarEventos(data = null) {
    let url = API_BASE;
    if (data) url += `?data=${encodeURIComponent(data)}`;

    try {
        const resp = await fetch(url, { credentials: "include" });

        if (!resp.ok) throw new Error("Falha ao buscar eventos");

        const eventos = await resp.json();
        renderizarEventos(eventos);

    } catch (erro) {
        console.error("Erro ao carregar eventos:", erro);
        alert("Erro ao carregar eventos. Veja o console.");
    }
}


// ================================
// RENDERIZAR EVENTOS NA LISTA
// ================================
function renderizarEventos(eventos) {
    const lista = document.querySelector("#lista-eventos");
    if (!lista) return;

    lista.innerHTML = "";

    if (!eventos || eventos.length === 0) {
        lista.innerHTML = "<p class='vazio'>Nenhum evento encontrado.</p>";
        return;
    }

    eventos.forEach(ev => {
        const item = document.createElement("div");
        item.classList.add("evento-item");

        item.innerHTML = `
            <h3>${ev.titulo}</h3>
            <p>${ev.descricao}</p>
            <p><strong>Data:</strong> ${ev.data}</p>
            <p><strong>Prioridade:</strong> ${ev.prioridade}</p>

            <button onclick="editarEvento(${ev.id})" class="btn-editar">Editar</button>
            <button onclick="deletarEvento(${ev.id})" class="btn-excluir">Excluir</button>
        `;

        lista.appendChild(item);
    });
}


// ================================
// FORMULÁRIO — CRIAR EVENTO
// ================================
function configurarFormulario() {
    const form = document.querySelector("#form-evento");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const dados = {
            titulo: form.titulo.value.trim(),
            descricao: form.descricao.value.trim(),
            data: form.data.value,
            prioridade: form.prioridade.value || "baixa",
            repeticao: form.repeticao?.value || null
        };

        // Validação simples
        if (!dados.titulo || !dados.data) {
            alert("Título e data são obrigatórios!");
            return;
        }

        try {
            const resp = await fetch(API_BASE, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dados),
                credentials: "include"
            });

            if (!resp.ok) {
                const erro = await resp.json().catch(() => ({}));
                alert(erro.erro || "Erro ao criar evento");
                return;
            }

            alert("Evento criado com sucesso!");
            form.reset();
            carregarEventos();

        } catch (erro) {
            console.error("Erro ao criar evento:", erro);
            alert("Erro ao criar evento. Veja o console.");
        }
    });
}


// ================================
// EDITAR EVENTO
// ================================
async function editarEvento(id) {
    const titulo = prompt("Novo título:");
    const desc = prompt("Nova descrição:");
    const data = prompt("Nova data (AAAA-MM-DD):");
    const prio = prompt("Prioridade (baixa, media, alta):");

    if (!titulo || !desc || !data) {
        alert("Todos os campos são obrigatórios!");
        return;
    }

    const dados = {
        titulo: titulo.trim(),
        descricao: desc.trim(),
        data,
        prioridade: prio || "baixa"
    };

    try {
        const resp = await fetch(`${API_BASE}/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dados),
            credentials: "include"
        });

        if (!resp.ok) {
            const erro = await resp.json().catch(() => ({}));
            alert(erro.erro || "Erro ao atualizar evento");
            return;
        }

        alert("Evento atualizado!");
        carregarEventos();

    } catch (erro) {
        console.error("Erro ao editar evento:", erro);
        alert("Erro ao atualizar evento. Veja o console.");
    }
}


// ================================
// DELETAR EVENTO
// ================================
async function deletarEvento(id) {
    if (!confirm("Deseja realmente excluir este evento?")) return;

    try {
        const resp = await fetch(`${API_BASE}/${id}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (!resp.ok) {
            const erro = await resp.json().catch(() => ({}));
            alert(erro.erro || "Erro ao excluir evento");
            return;
        }

        alert("Evento excluído!");
        carregarEventos();

    } catch (erro) {
        console.error("Erro ao excluir evento:", erro);
        alert("Erro ao excluir evento. Veja o console.");
    }
}
