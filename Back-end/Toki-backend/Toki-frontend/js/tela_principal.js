/* ===========================
   VARIÁVEIS GLOBAIS
=========================== */
let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();
let diaSelecionado = currentDate.getDate();
let usuarioLogado = null;
let editingEvent = null;
let eventosMes = [];

window.API_BASE = "http://localhost:8080/toki/evento";

const showEventsBtn = document.getElementById('showEventsBtn');
const eventsPanel = document.getElementById('eventsPanel');
const closeEventsPanel = document.getElementById('closeEventsPanel');
const eventsPanelList = document.getElementById('eventsPanelList');
const filterMonth = document.getElementById('filterMonth');
const filterPriority = document.getElementById('filterPriority');
const calendarGrid = document.querySelector('.calendar-grid');
const calendarMonth = document.getElementById('calendarMonth');

const EVENT_COLORS = { alta: "#ff2b2b", media: "#ffcc00", baixa: "#00c853" };
const MONTH_NAMES = ["Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                     "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"];
const WEEK_DAYS = ["Seg","Ter","Qua","Qui","Sex","Sáb","Dom"];

/* ===========================
   FERIADOS
=========================== */
function getHolidays(year){
    const fixed = [
        {dia:1, mes:1, nome:"Ano Novo"},
        {dia:21, mes:4, nome:"Tiradentes"},
        {dia:1, mes:5, nome:"Dia do Trabalho"},
        {dia:7, mes:9, nome:"Independência"},
        {dia:12, mes:10, nome:"Nossa Senhora Aparecida"},
        {dia:2, mes:11, nome:"Finados"},
        {dia:15, mes:11, nome:"Proclamação da República"},
        {dia:25, mes:12, nome:"Natal"}
    ];
    const easter = calcularPascoa(year);
    const carnival = new Date(easter.getTime()); carnival.setDate(carnival.getDate() - 47);
    const goodFriday = new Date(easter.getTime()); goodFriday.setDate(goodFriday.getDate() - 2);
    const corpusChristi = new Date(easter.getTime()); corpusChristi.setDate(easter.getDate() + 60);
    const movable = [
        {dia:carnival.getDate(), mes:carnival.getMonth()+1, nome:"Carnaval"},
        {dia:goodFriday.getDate(), mes:goodFriday.getMonth()+1, nome:"Sexta-Feira Santa"},
        {dia:easter.getDate(), mes:easter.getMonth()+1, nome:"Páscoa"},
        {dia:corpusChristi.getDate(), mes:corpusChristi.getMonth()+1, nome:"Corpus Christi"}
    ];
    return fixed.concat(movable);
}

/* ===========================
   PREENCHE SELECT DE MESES
=========================== */
MONTH_NAMES.forEach((m,i)=>{
    const option = document.createElement('option');
    option.value = i;
    option.textContent = m;
    if(i===currentMonth) option.selected=true;
    filterMonth.appendChild(option);
});

/* ===========================
   PAINEL “TODOS OS EVENTOS”
=========================== */
function renderEventsPanel(){
    const selectedMonth = parseInt(filterMonth.value);
    const selectedPriority = filterPriority.value;
    eventsPanelList.innerHTML = '';

    const filteredEvents = eventosMes
        .filter(ev=>{
            const evDate = new Date(ev.data);
            if(selectedMonth!==evDate.getMonth()) return false;
            if(selectedPriority && ev.prioridade!==selectedPriority) return false;
            return true;
        })
        .sort((a,b)=> new Date(a.data)-new Date(b.data));

    if(filteredEvents.length === 0){
        const emptyDiv = document.createElement('div');
        emptyDiv.textContent = 'Nenhum evento encontrado.';
        emptyDiv.style.padding = '10px';
        eventsPanelList.appendChild(emptyDiv);
        return;
    }

    filteredEvents.forEach(ev=>{
        const div = document.createElement('div');
        div.className = 'event-item';

        const header = document.createElement('div');
        header.className = 'event-header';
        header.innerHTML = `<span><span style="color:${EVENT_COLORS[ev.prioridade]}">●</span> ${ev.horaInicio||''} ${ev.titulo}</span>
                            <span class="expand-icon">▼</span>`;
        div.appendChild(header);

        const details = document.createElement('div');
        details.className = 'event-details hidden';
        details.innerHTML = `
            <p><strong>Descrição:</strong> ${ev.descricao||'-'}</p>
            <p><strong>Local:</strong> ${ev.local||'-'}</p>
            <p><strong>Fim:</strong> ${ev.horaFim||'-'}</p>
            <p><strong>Prioridade:</strong> ${ev.prioridade}</p>
            <div class="mt-2 flex gap-2">
                <button class="edit-btn">Editar</button>
                <button class="delete-btn">Excluir</button>
            </div>
        `;
        div.appendChild(details);

        header.addEventListener('click', ()=>{
            details.classList.toggle('hidden');
            header.querySelector('.expand-icon').textContent = details.classList.contains('hidden') ? '▼' : '▲';
        });

        details.querySelector('.edit-btn').addEventListener('click', e=>{
            e.stopPropagation();
            abrirModalEvento(ev);
        });
        details.querySelector('.delete-btn').addEventListener('click', async e=>{
            e.stopPropagation();
            if(confirm("Deseja realmente excluir este evento?")){
                try { await fetch(`${window.API_BASE}/${ev.id}`, {method:'DELETE', credentials:'include'}); }
                catch(err){ console.error(err); alert("Erro ao deletar evento"); }
                renderCalendar(currentMonth,currentYear);
                renderEventsPanel();
            }
        });

        eventsPanelList.appendChild(div);
    });
}

showEventsBtn.addEventListener('click', ()=>{ eventsPanel.classList.add('show'); renderEventsPanel(); });
closeEventsPanel.addEventListener('click', ()=>{ eventsPanel.classList.remove('show'); });
filterMonth.addEventListener('change', renderEventsPanel);
filterPriority.addEventListener('change', renderEventsPanel);

/* ===========================
   CALCULA PÁSCOA
=========================== */
function calcularPascoa(year){
    const a = year % 19;
    const b = Math.floor(year / 100);
    const c = year % 100;
    const d = Math.floor(b / 4);
    const e = b % 4;
    const f = Math.floor((b + 8) / 25);
    const g = Math.floor((b - f + 1) / 3);
    const h = (19 * a + b - d - g + 15) % 30;
    const i = Math.floor(c / 4);
    const k = c % 4;
    const l = (32 + 2*e + 2*i - h - k) % 7;
    const m = Math.floor((a + 11*h + 22*l) / 451);
    const month = Math.floor((h + l - 7*m + 114) / 31);
    const day = ((h + l - 7*m + 114) % 31) + 1;
    return new Date(year, month-1, day);
}

/* ===========================
   CALENDÁRIO
=========================== */
async function carregarEventosMes(month, year){
    try {
        const res = await fetch(`${window.API_BASE}?mes=${month+1}&ano=${year}`, {credentials:"include"});
        eventosMes = res.ok ? await res.json() : [];
    } catch(err){ console.error(err); eventosMes = []; }
}

async function renderCalendar(month, year){
    calendarMonth.textContent = `${MONTH_NAMES[month]} ${year}`;
    calendarGrid.innerHTML = "";
    await carregarEventosMes(month, year);

    const holidays = getHolidays(year);
    const firstDay = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();
    if(diaSelecionado>totalDays) diaSelecionado=totalDays;
    let emptyDays = firstDay===0 ? 6 : firstDay-1;

    for(let i=0;i<emptyDays;i++){
        const empty = document.createElement("div");
        empty.classList.add("day","empty");
        calendarGrid.appendChild(empty);
    }

    for(let day=1; day<=totalDays; day++){
        const dayDiv = document.createElement("div");
        dayDiv.className="day";
        const numberSpan = document.createElement("span");
        numberSpan.className="day-number";
        numberSpan.textContent = day;
        dayDiv.appendChild(numberSpan);

        const dayOfWeek = (emptyDays+day-1)%7;
        if(dayOfWeek===5||dayOfWeek===6) dayDiv.classList.add("weekend");

        const feriado = holidays.find(h=>h.dia===day && h.mes===month+1);
        if(feriado){ dayDiv.classList.add("holiday"); dayDiv.title=feriado.nome; }

        renderDayEvents(dayDiv, day, month, year);

        if(day===diaSelecionado && month===currentMonth && year===currentYear) dayDiv.classList.add('selected');
        if(day===currentDate.getDate() && month===currentDate.getMonth() && year===currentDate.getFullYear()) dayDiv.classList.add('today');

        calendarGrid.appendChild(dayDiv);
    }

    bindDaySelection();
    atualizarPainelLateralMesAtual();
}

/* ===========================
   RENDER DIA - BOLINHAS E POPOVER
=========================== */
function renderDayEvents(dayDiv, day, month, year){
    Array.from(dayDiv.children).forEach(child=>{ if(!child.classList.contains('day-number')) child.remove(); });

    const eventosDia = eventosMes.filter(ev=>{
        const evDate = new Date(ev.data + "T00:00:00");
        return evDate.getDate()===day && evDate.getMonth()===month && evDate.getFullYear()===year;
    });

    if(!eventosDia.length) return;

    const prioridade = { alta:1, media:2, baixa:3 };
    const sorted = eventosDia.sort((a,b)=> prioridade[a.prioridade]-prioridade[b.prioridade]).slice(0,3);

    const containerDots = document.createElement("div");
    containerDots.className="event-dots";
    sorted.forEach(ev=>{
        const dot = document.createElement("div");
        dot.className="event-dot";
        dot.style.backgroundColor = EVENT_COLORS[ev.prioridade];
        containerDots.appendChild(dot);
    });
    dayDiv.appendChild(containerDots);

    const popover = document.createElement("div");
    popover.className="day-popover";
    sorted.forEach(ev=>{
        const evDiv = document.createElement("div");
        evDiv.className="popover-event";
        evDiv.innerHTML = `<span class="popover-dot" style="background-color:${EVENT_COLORS[ev.prioridade]}"></span> ${ev.horaInicio||''} ${ev.titulo}`;
        evDiv.addEventListener('click', ()=> abrirModalEvento(ev));
        popover.appendChild(evDiv);
    });
    dayDiv.appendChild(popover);

    dayDiv.addEventListener("mouseenter", ()=> popover.style.opacity=1);
    dayDiv.addEventListener("mouseleave", ()=> popover.style.opacity=0);
    dayDiv.addEventListener('dblclick', ()=> abrirModalEvento({dia, mes: month+1, ano: year}));
}

/* ===========================
   PAINEL LATERAL DIÁRIO
=========================== */
function bindDaySelection(){
    document.querySelectorAll('.day').forEach(dayDiv=>{
        const dayNumSpan = dayDiv.querySelector('.day-number');
        if(!dayNumSpan) return;

        dayDiv.addEventListener('click', async ()=>{
            // Atualiza a seleção visual
            document.querySelectorAll('.day').forEach(d=>d.classList.remove('selected'));
            dayDiv.classList.add('selected');

            // Atualiza diaSelecionado antes de filtrar eventos
            diaSelecionado = parseInt(dayNumSpan.textContent);
            document.getElementById('selectedDayTitle').textContent=`Dia ${diaSelecionado}`;

            // Filtra e atualiza painel lateral com eventos do dia clicado
            const eventos = eventosMes.filter(ev=>{
                const evDate = new Date(ev.data);
                return evDate.getDate()===diaSelecionado &&
                       evDate.getMonth()===currentMonth &&
                       evDate.getFullYear()===currentYear;
            });

            atualizarPainelLateral(eventos);
        });
    });
}

function atualizarPainelLateral(eventos){
    const eventsList = document.getElementById('eventsList');
    eventsList.innerHTML='';

    eventos.forEach(ev=>{
        const div = document.createElement('div');
        div.className = 'event-item';

        const header = document.createElement('div');
        header.className = 'event-header';
        header.innerHTML = `<span><span style="color:${EVENT_COLORS[ev.prioridade]}">●</span> ${ev.horaInicio||''} ${ev.titulo}</span>
                            <span class="expand-icon">▼</span>`;
        div.appendChild(header);

        const details = document.createElement('div');
        details.className = 'event-details hidden';
        details.innerHTML = `
            <p><strong>Descrição:</strong> ${ev.descricao||'-'}</p>
            <p><strong>Local:</strong> ${ev.local||'-'}</p>
            <p><strong>Fim:</strong> ${ev.horaFim||'-'}</p>
            <p><strong>Prioridade:</strong> ${ev.prioridade}</p>
        `;
        div.appendChild(details);

        header.addEventListener('click', ()=>{
            details.classList.toggle('hidden');
            header.querySelector('.expand-icon').textContent = details.classList.contains('hidden') ? '▼' : '▲';
        });

        div.addEventListener('dblclick', ()=> abrirModalEvento(ev));

        eventsList.appendChild(div);
    });
}

async function atualizarPainelLateralMesAtual(){
    const eventos = eventosMes.filter(ev=>{
        const evDate = new Date(ev.data);
        return evDate.getDate()===diaSelecionado &&
               evDate.getMonth()===currentMonth &&
               evDate.getFullYear()===currentYear;
    });
    atualizarPainelLateral(eventos);
}

/* ===========================
   MODAL EVENTOS
=========================== */
const eventModal = document.getElementById('eventModal');
const createEventBtn = document.getElementById('createEventBtn');
const closeModalBtn = document.getElementById('closeModalBtn');
const saveEventBtn = document.getElementById('saveEventBtn');

function abrirModalEvento(event={}) {
    editingEvent = event.id ? {...event} : null;
    document.getElementById('modalTitle').textContent = editingEvent?"Editar Evento":"Criar Evento";

    const dataEvento = event.dia ?
        `${event.ano}-${String(event.mes).padStart(2,'0')}-${String(event.dia).padStart(2,'0')}` :
        `${currentYear}-${String(currentMonth+1).padStart(2,'0')}-${String(diaSelecionado).padStart(2,'0')}`;

    document.getElementById('eventDate').value = dataEvento;
    document.getElementById('eventTitle').value = event.titulo||'';
    document.getElementById('eventDescription').value = event.descricao||'';
    document.getElementById('eventStartTime').value = event.horaInicio||getCurrentHourRounded();
    document.getElementById('eventEndTime').value = event.horaFim||'';
    document.getElementById('eventLocation').value = event.local||'';
    document.getElementById('eventPriority').value = event.prioridade||'baixa';
    eventModal.classList.add('show');
}

function getCurrentHourRounded(){
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes()<30?'00':'30';
    return `${String(hours).padStart(2,'0')}:${minutes}`;
}

createEventBtn.addEventListener('click', ()=> abrirModalEvento());
closeModalBtn.addEventListener('click', ()=> { eventModal.classList.remove('show'); limparModal(); });

saveEventBtn.addEventListener('click', async ()=>{
    const titulo = document.getElementById('eventTitle').value.trim();
    const descricao = document.getElementById('eventDescription').value.trim();
    const data = document.getElementById('eventDate').value;
    const horaInicio = document.getElementById('eventStartTime').value;
    const horaFim = document.getElementById('eventEndTime').value;
    const local = document.getElementById('eventLocation').value.trim();
    const prioridade = document.getElementById('eventPriority').value;

    if(!titulo||!data){ alert("Título e data são obrigatórios."); return; }

    const payload = { titulo, descricao, data, horaInicio, horaFim, local, prioridade, usuarioId:usuarioLogado?.id };

    try {
        const method = editingEvent ? 'PUT' : 'POST';
        const url = editingEvent ? `${window.API_BASE}/${editingEvent.id}` : window.API_BASE;
        await fetch(url, {method, body: JSON.stringify(payload), headers:{'Content-Type':'application/json'}, credentials:'include'});
        eventModal.classList.remove('show');
        limparModal();
        renderCalendar(currentMonth,currentYear);
        renderEventsPanel();
    } catch(err){ console.error(err); alert("Erro ao salvar evento."); }
});

function limparModal(){
    editingEvent=null;
    document.getElementById('eventForm').reset();
}

/* ===========================
   INICIALIZAÇÃO
=========================== */
document.addEventListener('DOMContentLoaded', ()=>{
    renderCalendar(currentMonth,currentYear);
});
