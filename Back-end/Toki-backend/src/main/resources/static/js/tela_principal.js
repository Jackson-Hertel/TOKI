// ==========================
// TOKI - JS Principal
// ==========================

// === CALENDÁRIO ===
const calendarDays = document.getElementById("calendarDays");
const monthYear = document.getElementById("monthYear");
const months = ["Janeiro","Fevereiro","Março","Abril","Maio","Junho",
  "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"];
let date = new Date();
let today = new Date();
let selectedDate = null;

// Renderiza calendário
function renderCalendar() {
    const year = date.getFullYear();
    const month = date.getMonth();
    monthYear.textContent = `${months[month]} ${year}`;

    const firstDay = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();
    calendarDays.innerHTML = "";

    for (let i = 0; i < firstDay; i++) {
        const empty = document.createElement("div");
        empty.classList.add("empty");
        calendarDays.appendChild(empty);
    }

    for (let d = 1; d <= lastDate; d++) {
        const day = document.createElement("div");
        day.textContent = d;

        if(d === today.getDate() && month === today.getMonth() && year === today.getFullYear()){
            day.classList.add("today");
        }

        day.addEventListener("click", () => {
            document.querySelectorAll(".calendar-grid div").forEach(el => el.classList.remove("selected"));
            day.classList.add("selected");
            selectedDate = new Date(year, month, d);
            openReminderForm(selectedDate);
        });

        calendarDays.appendChild(day);
    }
}

// Abrir formulário de lembrete
function openReminderForm(date){
    const form = document.getElementById("addReminderContainer");
    form.style.display = "block";
    const dateInput = document.getElementById("reminderData");
    dateInput.value = date.toISOString().split("T")[0];
}

// Fechar formulário
document.getElementById("cancelReminderBtn").addEventListener("click", ()=>{
    document.getElementById("addReminderContainer").style.display="none";
    document.getElementById("saveReminderBtn").textContent = "Salvar";
    document.getElementById("saveReminderBtn").onclick = salvarLembrete;
});

// === DROPDOWN DE PERFIL ===
const avatarBtn = document.getElementById("avatarBtn");
const dropdownMenu = document.getElementById("dropdownMenu");
if(avatarBtn && dropdownMenu){
    avatarBtn.addEventListener("click", e=>{
        e.stopPropagation();
        dropdownMenu.style.display = dropdownMenu.style.display==="block"?"none":"block";
    });
    window.addEventListener("click", e=>{
        if(!e.target.closest(".profile-container")) dropdownMenu.style.display="none";
    });
}

// === AVATAR ===
const avatarInput = document.getElementById("avatarInput");
const userAvatar = document.getElementById("userAvatar");
const dropdownAvatar = document.getElementById("dropdownAvatar");
const changePhotoBtn = document.getElementById("changePhotoBtn");
const savedAvatar = localStorage.getItem("toki-avatar");
if(savedAvatar){userAvatar.src=savedAvatar; dropdownAvatar.src=savedAvatar;}
if(changePhotoBtn) changePhotoBtn.addEventListener("click", ()=>avatarInput.click());
if(avatarInput){
    avatarInput.addEventListener("change", ()=>{
        const file = avatarInput.files[0];
        if(!file) return;
        const reader = new FileReader();
        reader.onload = e=>{
            const imgData = e.target.result;
            userAvatar.src = imgData; dropdownAvatar.src = imgData;
            localStorage.setItem("toki-avatar", imgData);
        };
        reader.readAsDataURL(file);
    });
}

// === USUÁRIO LOGADO ===
async function carregarUsuarioLogado(){
    const nameElem = document.getElementById("userName");
    const emailElem = document.getElementById("userEmail");
    const nameTop = document.getElementById("userNameTop");
    const emailTop = document.getElementById("userEmailTop");
    try{
        const res = await fetch("http://localhost:8080/api/usuarios/logado");
        if(!res.ok) throw new Error("Usuário não logado");
        const usuario = await res.json();
        const nome = usuario.nome || "Usuário";
        const email = usuario.email || "sememail@toki.com";
        nameElem.textContent = nome; emailElem.textContent = email;
        nameTop.textContent = nome; emailTop.textContent = email;
        localStorage.setItem("toki-nome",nome);
        localStorage.setItem("toki-email",email);
    }catch{
        const nomeSalvo = localStorage.getItem("toki-nome")||"Usuário";
        const emailSalvo = localStorage.getItem("toki-email")||"sememail@toki.com";
        nameElem.textContent = nomeSalvo; emailElem.textContent = emailSalvo;
        nameTop.textContent = nomeSalvo; emailTop.textContent = emailSalvo;
    }
}

// === TEMA ===
function applyTheme(theme){
    document.documentElement.setAttribute("data-theme", theme);
    localStorage.setItem("toki-theme", theme);
}
(function initTheme(){
    const saved = localStorage.getItem("toki-theme")||"system";
    let finalTheme = saved;
    if(saved==="system") finalTheme = window.matchMedia("(prefers-color-scheme: dark)").matches?"dark":"light";
    applyTheme(finalTheme);
})();
const toggleThemeBtn = document.getElementById("toggleTheme");
if(toggleThemeBtn) toggleThemeBtn.addEventListener("click", ()=>{
    const current = document.documentElement.getAttribute("data-theme");
    applyTheme(current==="dark"?"light":"dark");
});

// === ALTERNAR CALENDÁRIO / LEMBRETES ===
const btnCalendario = document.getElementById("btnCalendario");
const btnLembretes = document.getElementById("btnLembretes");
const calendarSection = document.getElementById("calendarSection");
const reminderSection = document.getElementById("reminderSection");

function showCalendar(){
    calendarSection.style.display="block";
    reminderSection.style.display="none";
    btnCalendario.classList.add("active");
    btnLembretes.classList.remove("active");
}
function showReminders(){
    calendarSection.style.display="none";
    reminderSection.style.display="block";
    btnLembretes.classList.add("active");
    btnCalendario.classList.remove("active");
    carregarLembretes();
}
btnCalendario.addEventListener("click", showCalendar);
btnLembretes.addEventListener("click", showReminders);
showCalendar();

// === CARREGAR LEMBRETES COM EDITAR/EXCLUIR ===
const reminderList = document.getElementById("reminderList");

async function carregarLembretes() {
    try {
        const res = await fetch("http://localhost:8080/toki/evento");
        if (!res.ok) throw new Error("Erro ao carregar eventos");
        const eventos = await res.json();
        reminderList.innerHTML = "";

        eventos.forEach(ev => {
            const li = document.createElement("li");
            li.classList.add("reminder-item");

            const textDiv = document.createElement("div");
            textDiv.textContent = `${ev.titulo} - ${ev.descricao} (${ev.data})`;
            li.appendChild(textDiv);

            const btnContainer = document.createElement("div");
            btnContainer.classList.add("reminder-buttons-item");

            const editBtn = document.createElement("button");
            editBtn.textContent = "Editar";
            editBtn.addEventListener("click", () => editarLembrete(ev));

            const deleteBtn = document.createElement("button");
            deleteBtn.textContent = "Excluir";
            deleteBtn.addEventListener("click", () => excluirLembrete(ev.id));

            btnContainer.appendChild(editBtn);
            btnContainer.appendChild(deleteBtn);
            li.appendChild(btnContainer);

            reminderList.appendChild(li);
        });
    } catch (err) {
        console.error(err);
    }
}

// Salvar lembrete
async function salvarLembrete() {
    const titulo = document.getElementById("reminderTitulo").value.trim();
    const descricao = document.getElementById("reminderDescricao").value.trim();
    const data = document.getElementById("reminderData").value;
    const prioridade = parseInt(document.getElementById("reminderPrioridade").value);

    if (!titulo || !descricao || !data) return alert("Preencha todos os campos.");

    try {
        const res = await fetch("http://localhost:8080/toki/evento", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ titulo, descricao, data, prioridade })
        });
        if (!res.ok) throw new Error("Erro ao salvar lembrete");
        alert("Lembrete salvo!");
        document.getElementById("addReminderContainer").style.display = "none";
        carregarLembretes();
    } catch (err) { console.error(err); }
}

// Editar lembrete
function editarLembrete(ev) {
    document.getElementById("addReminderContainer").style.display = "block";
    document.getElementById("reminderTitulo").value = ev.titulo;
    document.getElementById("reminderDescricao").value = ev.descricao;
    document.getElementById("reminderData").value = ev.data;
    document.getElementById("reminderPrioridade").value = ev.prioridade;
    selectedDate = new Date(ev.data);

    const saveBtn = document.getElementById("saveReminderBtn");
    saveBtn.textContent = "Atualizar";
    saveBtn.onclick = async () => {
        const titulo = document.getElementById("reminderTitulo").value.trim();
        const descricao = document.getElementById("reminderDescricao").value.trim();
        const data = document.getElementById("reminderData").value;
        const prioridade = parseInt(document.getElementById("reminderPrioridade").value);

        if (!titulo || !descricao || !data) return alert("Preencha todos os campos.");

        try {
            const res = await fetch(`http://localhost:8080/toki/evento/${ev.id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ titulo, descricao, data, prioridade })
            });
            if (!res.ok) throw new Error("Erro ao atualizar lembrete");
            alert("Lembrete atualizado!");
            document.getElementById("addReminderContainer").style.display = "none";
            saveBtn.textContent = "Salvar";
            saveBtn.onclick = salvarLembrete;
            carregarLembretes();
        } catch (err) { console.error(err); }
    };
}

// Excluir lembrete
async function excluirLembrete(id) {
    if (!confirm("Deseja realmente excluir este lembrete?")) return;
    try {
        const res = await fetch(`http://localhost:8080/toki/evento/${id}`, {
            method: "DELETE"
        });
        if (!res.ok) throw new Error("Erro ao excluir lembrete");
        alert("Lembrete excluído!");
        carregarLembretes();
    } catch (err) { console.error(err); }
}

// Inicialização
renderCalendar();
carregarUsuarioLogado();
carregarLembretes();
