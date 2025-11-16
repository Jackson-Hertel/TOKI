/* ===========================
   TOKI - Tela Principal JS
   =========================== */

// ===================
// Elementos DOM
// ===================
const calendarGrid = document.querySelector('.calendar-grid');
const calendarMonth = document.getElementById('calendarMonth');
const prevMonthBtn = document.getElementById('prevMonth');
const nextMonthBtn = document.getElementById('nextMonth');

const rightPanel = document.getElementById('rightPanel');
const selectedDayTitle = document.getElementById('selectedDayTitle');
const selectedDayInfo = document.getElementById('selectedDayInfo');
const eventsList = document.getElementById('eventsList');
const tasksList = document.getElementById('tasksList');
const eventsTasksList = document.getElementById('eventsTasksList');

const createEventBtn = document.getElementById('createEventBtn');
const settingsBtn = document.getElementById('settingsBtn');

const profileContainer = document.getElementById('profileContainer');
const profileInput = document.getElementById('profileInput');
const profileImage = document.getElementById('profileImage');

const eventModal = document.getElementById('eventModal');
const saveEventBtn = document.getElementById('saveEventBtn');
const closeModalBtn = document.getElementById('closeModalBtn');

// ===================
// Variáveis
// ===================
let today = new Date();
let currentMonth = today.getMonth();
let currentYear = today.getFullYear();
let selectedCell = null;
let selectedDateKey = null;

let events = {};
let tasks = {};
let usuarioLogado = null;

// ===================
// Funções do Calendário
// ===================
function renderCalendar(month, year) {
    calendarGrid.innerHTML = '';

    const firstDay = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();

    const monthNames = ["Janeiro","Fevereiro","Março","Abril","Maio","Junho",
        "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"];
    calendarMonth.textContent = `${monthNames[month]} ${year}`;

    const weekDays = ["DOM","SEG","TER","QUA","QUI","SEX","SAB"];
    weekDays.forEach(day => {
        const div = document.createElement('div');
        div.textContent = day;
        div.classList.add('font-bold','text-center');
        calendarGrid.appendChild(div);
    });

    for (let i = 0; i < firstDay; i++) {
        calendarGrid.appendChild(document.createElement('div'));
    }

    for (let day = 1; day <= lastDate; day++) {
        const dayDiv = document.createElement('div');
        dayDiv.classList.add('cursor-pointer','p-3','rounded','text-center','hover:bg-gray-200');
        dayDiv.textContent = day;

        if (day === today.getDate() && month === today.getMonth() && year === today.getFullYear()) {
            dayDiv.classList.add('today','border','border-blue-600','font-bold','text-blue-600');
        }

        dayDiv.addEventListener('click', () => toggleDay(dayDiv, day, month, year));
        calendarGrid.appendChild(dayDiv);
    }
}

function toggleDay(cell, day, month, year) {
    if (selectedCell === cell) {
        cell.classList.remove('selected');
        selectedCell = null;
        rightPanel.classList.add('hidden');
        selectedDateKey = null;
    } else {
        if (selectedCell) selectedCell.classList.remove('selected');
        cell.classList.add('selected');
        selectedCell = cell;
        selectedDateKey = `${year}-${(month+1).toString().padStart(2,'0')}-${day.toString().padStart(2,'0')}`;
        rightPanel.classList.remove('hidden');
        selectedDayTitle.textContent = `${day}/${month+1}/${year}`;
        updateRightPanel();
    }
}

function updateRightPanel() {
    eventsList.innerHTML = '';
    if (events[selectedDateKey]) {
        events[selectedDateKey].forEach(ev => {
            const div = document.createElement('div');
            div.textContent = `${ev.hora} - ${ev.nome}`;
            div.classList.add('p-2','bg-white','text-black','rounded');
            eventsList.appendChild(div);
        });
    } else {
        eventsList.innerHTML = '<p class="text-sm">Nenhum evento</p>';
    }

    tasksList.innerHTML = '';
    if (tasks[selectedDateKey]) {
        tasks[selectedDateKey].forEach(tsk => {
            const div = document.createElement('div');
            div.textContent = tsk;
            div.classList.add('p-2','bg-white','text-black','rounded');
            tasksList.appendChild(div);
        });
    } else {
        tasksList.innerHTML = '<p class="text-sm">Nenhuma tarefa</p>';
    }

    eventsTasksList.innerHTML = '';
    if ((events[selectedDateKey]?.length || 0) + (tasks[selectedDateKey]?.length || 0) > 0) {
        if (events[selectedDateKey]) {
            events[selectedDateKey].forEach(ev => {
                const div = document.createElement('div');
                div.textContent = `Evento: ${ev.hora} - ${ev.nome}`;
                div.classList.add('p-2','bg-white','text-black','rounded');
                eventsTasksList.appendChild(div);
            });
        }
        if (tasks[selectedDateKey]) {
            tasks[selectedDateKey].forEach(tsk => {
                const div = document.createElement('div');
                div.textContent = `Tarefa: ${tsk}`;
                div.classList.add('p-2','bg-white','text-black','rounded');
                eventsTasksList.appendChild(div);
            });
        }
    } else {
        eventsTasksList.innerHTML = '<p class="text-sm">Nenhum evento ou tarefa</p>';
    }

    selectedDayInfo.textContent = `${events[selectedDateKey]?.length || 0} eventos, ${tasks[selectedDateKey]?.length || 0} tarefas`;
}

// ===================
// Navegação de meses
// ===================
prevMonthBtn.addEventListener('click', () => {
    currentMonth--;
    if (currentMonth < 0) { currentMonth = 11; currentYear--; }
    renderCalendar(currentMonth, currentYear);
});

nextMonthBtn.addEventListener('click', () => {
    currentMonth++;
    if (currentMonth > 11) { currentMonth = 0; currentYear++; }
    renderCalendar(currentMonth, currentYear);
});

// ===================
// Modal de evento
// ===================
createEventBtn.addEventListener('click', () => {
    if (!selectedDateKey) return alert('Selecione um dia antes!');
    document.getElementById('eventName').value = '';
    document.getElementById('eventTime').value = '';
    document.getElementById('eventType').value = 'blue';
    eventModal.classList.remove('hidden');
});

closeModalBtn.addEventListener('click', () => eventModal.classList.add('hidden'));

saveEventBtn.addEventListener('click', () => {
    const name = document.getElementById('eventName').value.trim();
    const time = document.getElementById('eventTime').value;
    const type = document.getElementById('eventType').value;
    if (!name || !time) return alert('Preencha nome e horário do evento!');
    if (!events[selectedDateKey]) events[selectedDateKey] = [];
    events[selectedDateKey].push({ nome: name, hora: time, tipo: type });
    eventModal.classList.add('hidden');
    updateRightPanel();
});

// ===================
// Configurações
// ===================
settingsBtn.addEventListener('click', () => {
    window.location.href = 'configuracoes_usuario.html';
});

// ===================
// Perfil e foto
// ===================
profileContainer.addEventListener('click', () => profileInput.click());

profileInput.addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (!file || !usuarioLogado) return;

    const formData = new FormData();
    formData.append('fotoPerfil', file);

    try {
        const res = await fetch('/api/usuarios/upload', {
            method: 'POST',
            credentials: 'include',
            body: formData
        });
        if (res.ok) {
            const data = await res.json();
            // Atualiza apenas com o caminho do arquivo, não Base64
            profileImage.src = data.caminho;
            usuarioLogado.fotoPerfil = data.caminho;
        } else {
            console.error('Erro ao atualizar foto');
        }
    } catch (err) {
        console.error('Erro na requisição:', err);
    }
});

// ===================
// Carregar usuário logado
// ===================
async function carregarUsuarioLogado() {
    try {
        const res = await fetch('/api/usuarios/logado', { credentials: 'include' });
        if (res.ok) {
            usuarioLogado = await res.json();
            if (usuarioLogado.fotoPerfil) profileImage.src = usuarioLogado.fotoPerfil;
        } else {
            console.warn('Nenhum usuário logado');
        }
    } catch (err) {
        console.error('Erro ao carregar usuário logado:', err);
    }
}

// ===================
// Inicialização
// ===================
window.addEventListener('DOMContentLoaded', () => {
    carregarUsuarioLogado();
    renderCalendar(currentMonth, currentYear);
});
