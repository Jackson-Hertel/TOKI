/* tela_principal.js - versão completa com painel de eventos filtrável */

// ===========================
// TEMA
// ===========================
window.aplicarTema = function aplicarTema() {
    const temaLS = localStorage.getItem("toki-tema");
    const corLS = localStorage.getItem("toki-cor");

    if (temaLS) {
        document.documentElement.setAttribute(
            "data-theme",
            temaLS === "escuro" ? "dark" : "light"
        );
    } else {
        document.documentElement.setAttribute("data-theme", "light");
    }

    if (corLS) {
        document.documentElement.style.setProperty("--toki-primary", corLS);
    }
};
window.aplicarTema();
window.addEventListener("storage", (event) => {
    if (event.key === "toki-tema" || event.key === "toki-cor") window.aplicarTema();
});

// ===========================
// CONFIGURAÇÃO INICIAL
// ===========================
(() => {
    const BASE = (window.API_BASE || "").replace(/\/$/, "");
    const EVENT_API = BASE + "/evento";
    const USER_API = BASE + "/usuario";

    let currentDate = new Date();
    let currentMonth = currentDate.getMonth();
    let currentYear = currentDate.getFullYear();
    let selectedDay = currentDate.toISOString().split("T")[0];
    let usuarioLogado = null;
    let editingEvent = null;
    let eventosMes = [];

    const EVENT_COLORS = { alta: "#ff2b2b", media: "#ffcc00", baixa: "#00c853" };
    const MONTH_NAMES = ["Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                         "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"];

    // ===========================
    // UTILITÁRIOS
    // ===========================
    function getCurrentHourRounded(){
        const now = new Date();
        const hours = now.getHours();
        const minutes = now.getMinutes() < 30 ? '00' : '30';
        return `${String(hours).padStart(2,'0')}:${minutes}`;
    }

    function limparModal(){
        editingEvent = null;
        const form = document.getElementById('form-evento');
        if (form) form.reset();
        document.getElementById('eventId')?.removeAttribute('value');
    }

    // ===========================
    // FOTO DE PERFIL
    // ===========================
    function atualizarFotoPerfil() {
        if (!usuarioLogado) return;

        const img = document.getElementById('profileImage');
        if (!img) return;

        // Se houver foto no banco, usa Base64; caso contrário, imagem padrão
        const src = usuarioLogado.fotoPerfil && usuarioLogado.fotoPerfil.toString().trim() !== ""
            ? `data:image/png;base64,${usuarioLogado.fotoPerfil}`
            : '../img/default-profile.jpg';

        img.src = src;
    }


    async function enviarFotoParaServidor(base64Data) {
        try {
            const res = await fetch(`${USER_API}/foto`, {
                method: 'PUT',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ foto: base64Data })
            });
            if (res.ok) {
                const updated = await res.json().catch(()=>null);
                if (updated) { usuarioLogado = updated; atualizarFotoPerfil(); }
            }
        } catch (err) { console.warn(err); }
    }

    async function carregarUsuarioLogado() {
        try {
            const res = await fetch(USER_API + "/logado", { credentials: 'include' });
            if (!res.ok) throw new Error('Usuário não logado');
            usuarioLogado = await res.json();
            atualizarFotoPerfil();

            const input = document.getElementById('profileImageInput');
            if (input) {
                input.addEventListener('change', async (e) => {
                    const file = e.target.files && e.target.files[0];
                    if (!file) return;
                    const reader = new FileReader();
                    reader.onload = async (ev) => {
                        const dataUrl = ev.target.result;
                        document.querySelectorAll('.profile-img').forEach(img => img.src = dataUrl);
                        const base64 = dataUrl.split(',')[1];
                        await enviarFotoParaServidor(base64);
                    };
                    reader.readAsDataURL(file);
                });
            }

            document.getElementById('settingsBtn')?.addEventListener('click', ()=> window.location.href = '/tela_principal/configuracoes_usuario.html');
            document.getElementById('notificationsBtn')?.addEventListener('click', ()=> alert(`Notificações de ${usuarioLogado.nome || 'usuário'} ainda não implementadas.`));
        } catch(err) { console.warn(err); }
    }

    // ===========================
    // FERIADOS
    // ===========================
    function calcularPascoa(year){
        const a = year % 19, b = Math.floor(year/100), c = year % 100;
        const d = Math.floor(b/4), e = b%4, f = Math.floor((b+8)/25), g = Math.floor((b-f+1)/3);
        const h = (19*a + b - d - g + 15) % 30;
        const i = Math.floor(c/4), k = c % 4;
        const l = (32 + 2*e + 2*i - h - k) % 7;
        const m = Math.floor((a + 11*h + 22*l)/451);
        const month = Math.floor((h + l - 7*m + 114)/31);
        const day = ((h + l - 7*m + 114) % 31) + 1;
        return new Date(year, month-1, day);
    }

    function getHolidays(year){
        const fixed = [
            {dia:1, mes:1, nome:"Ano Novo"}, {dia:21, mes:4, nome:"Tiradentes"},
            {dia:1, mes:5, nome:"Dia do Trabalho"}, {dia:7, mes:9, nome:"Independência"},
            {dia:12, mes:10, nome:"Nossa Senhora Aparecida"}, {dia:2, mes:11, nome:"Finados"},
            {dia:15, mes:11, nome:"Proclamação da República"}, {dia:25, mes:12, nome:"Natal"}
        ];
        const easter = calcularPascoa(year);
        const carnival = new Date(easter.getTime()); carnival.setDate(easter.getDate() - 47);
        const goodFriday = new Date(easter.getTime()); goodFriday.setDate(easter.getDate() - 2);
        const corpusChristi = new Date(easter.getTime()); corpusChristi.setDate(easter.getDate() + 60);
        const movable = [
            {dia:carnival.getDate(), mes:carnival.getMonth()+1, nome:"Carnaval"},
            {dia:goodFriday.getDate(), mes:goodFriday.getMonth()+1, nome:"Sexta-Feira Santa"},
            {dia:easter.getDate(), mes:easter.getMonth()+1, nome:"Páscoa"},
            {dia:corpusChristi.getDate(), mes:corpusChristi.getMonth()+1, nome:"Corpus Christi"}
        ];
        return fixed.concat(movable);
    }

    // ===========================
    // EVENTOS
    // ===========================
    async function carregarEventosMes(month, year){
        try{
            const res = await fetch(`${EVENT_API}?mes=${month+1}&ano=${year}`, {credentials:"include"});
            eventosMes = res.ok ? await res.json() : [];
        }catch(err){ console.error(err); eventosMes = []; }
    }

    function renderDayEvents(dayDiv, dateISO) {
        dayDiv.querySelectorAll('.event-dots').forEach(el => el.remove());
        const eventosDia = eventosMes.filter(ev => ev.data.split('T')[0] === dateISO);
        if (!eventosDia.length) return;

        const containerDots = document.createElement("div");
        containerDots.className = "event-dots";
        eventosDia.slice(0, 3).forEach(ev => {
            const dot = document.createElement("div");
            dot.style.backgroundColor = EVENT_COLORS[ev.prioridade] || "#3f6bff";
            containerDots.appendChild(dot);
        });
        dayDiv.appendChild(containerDots);
    }

    // ===========================
    // CALENDÁRIO
    // ===========================
    document.getElementById('prevMonthBtn')?.addEventListener('click', () => {
        currentMonth--; if (currentMonth < 0) { currentMonth = 11; currentYear--; }
        renderCalendar(currentMonth, currentYear);
    });

    document.getElementById('nextMonthBtn')?.addEventListener('click', () => {
        currentMonth++; if (currentMonth > 11) { currentMonth = 0; currentYear++; }
        renderCalendar(currentMonth, currentYear);
    });

    async function renderCalendar(month, year) {
        const calendarGrid = document.getElementById('calendarGrid');
        const calendarMonth = document.getElementById('calendarMonth');
        if (!calendarGrid || !calendarMonth) return;

        calendarMonth.textContent = `${MONTH_NAMES[month]} ${year}`;
        calendarGrid.innerHTML = "";

        await carregarEventosMes(month, year);

        const holidays = getHolidays(year);
        const firstDay = new Date(year, month, 1).getDay();
        const totalDays = new Date(year, month + 1, 0).getDate();
        let emptyDays = firstDay === 0 ? 6 : firstDay - 1;

        for (let i = 0; i < emptyDays; i++) {
            const empty = document.createElement("div");
            empty.classList.add("day", "empty");
            calendarGrid.appendChild(empty);
        }

        for (let day = 1; day <= totalDays; day++) {
            const dayDiv = document.createElement("div");
            dayDiv.className = "day";

            const numberSpan = document.createElement("span");
            numberSpan.className = "day-number"; numberSpan.textContent = day;
            dayDiv.appendChild(numberSpan);

            const dateISO = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            dayDiv.dataset.date = dateISO;

            const dayOfWeek = (emptyDays + day - 1) % 7;
            if (dayOfWeek === 5 || dayOfWeek === 6) dayDiv.classList.add("weekend");

            const feriado = holidays.find(h => h.dia === day && h.mes === month + 1);
            if (feriado) {
                dayDiv.classList.add("holiday");
                dayDiv.title = feriado.nome;
                const fLabel = document.createElement('div');
                fLabel.className = 'holiday-name'; fLabel.textContent = feriado.nome;
                dayDiv.appendChild(fLabel);
            }

            renderDayEvents(dayDiv, dateISO);

            if (dateISO === selectedDay) dayDiv.classList.add('selected');
            if (day === currentDate.getDate() && month === currentDate.getMonth() && year === currentDate.getFullYear()) {
                dayDiv.classList.add('today');
            }

            calendarGrid.appendChild(dayDiv);
        }

        bindDaySelection();
    }

    // ===========================
    // PAINEL LATERAL (DIA SELECIONADO)
    // ===========================
    function atualizarPainelLateral(eventos) {
        const eventsList = document.getElementById('eventsList');
        if (!eventsList) return;

        eventsList.innerHTML = '';

        if (!eventos || eventos.length === 0) {
            eventsList.innerHTML = '<p class="text-gray-600">Nenhum evento pendente para este dia</p>';
            return;
        }

        const header = document.createElement('p');
        header.className = 'text-gray-700 font-semibold';
        header.textContent = 'Eventos pendentes:';
        eventsList.appendChild(header);

        eventos.forEach(ev => {
            const div = document.createElement('div');
            div.className = 'event-item';

            const headerEv = document.createElement('div');
            headerEv.className = 'event-header';
            headerEv.innerHTML = `<span><span style="color:${EVENT_COLORS[ev.prioridade] || '#3f6bff'}">●</span> ${ev.horaInicio||''} ${ev.titulo}</span>
                                   <span class="expand-icon">▼</span>`;
            div.appendChild(headerEv);

            const details = document.createElement('div');
            details.className = 'event-details hidden';
            details.innerHTML = `
                <p><strong>Data:</strong> ${ev.data.split('T')[0]}</p>
                <p><strong>Descrição:</strong> ${ev.descricao||'-'}</p>
                <p><strong>Local:</strong> ${ev.local||'-'}</p>
                <p><strong>Fim:</strong> ${ev.horaFim||'-'}</p>
                <p><strong>Prioridade:</strong> ${ev.prioridade}</p>
            `;
            div.appendChild(details);

            headerEv.addEventListener('click', () => {
                details.classList.toggle('hidden');
                headerEv.querySelector('.expand-icon').textContent = details.classList.contains('hidden') ? '▼' : '▲';
            });

            eventsList.appendChild(div);
        });
    }

    // ===========================
    // CLIQUE NOS DIAS
    // ===========================
    function bindDaySelection() {
        const rightPanel = document.getElementById('rightPanel');
        document.querySelectorAll('.day').forEach(dayDiv => {
            const dateISO = dayDiv.dataset.date;
            if (!dateISO) return;

            dayDiv.addEventListener('click', () => {
                document.querySelectorAll('.day').forEach(d => d.classList.remove('selected'));
                dayDiv.classList.add('selected');
                selectedDay = dateISO;

                const eventosDia = eventosMes.filter(ev => ev.data.split('T')[0] === dateISO);

                if (rightPanel) { rightPanel.classList.remove('hidden'); rightPanel.classList.add('show'); }

                atualizarPainelLateral(eventosDia);
            });
        });
    }

    // ===========================
    // MODAL DE EVENTOS
    // ===========================
    function abrirModalEvento(event = {}) {
        editingEvent = event && event.id ? { ...event } : null;
        document.getElementById('modalTitle').textContent = editingEvent ? "Editar Evento" : "Criar Evento";

        const dataEvento = event && event.data
            ? event.data.toString().substring(0, 10)
            : selectedDay;

        document.getElementById('eventId').value = editingEvent ? editingEvent.id : '';
        document.getElementById('eventDate').value = dataEvento;
        document.getElementById('eventTitle').value = event.titulo || '';
        document.getElementById('eventDescription').value = event.descricao || '';
        document.getElementById('eventStartTime').value = event.horaInicio || getCurrentHourRounded();
        document.getElementById('eventEndTime').value = event.horaFim || '';
        document.getElementById('eventLocation').value = event.local || '';
        document.getElementById('eventPriority').value = event.prioridade || 'baixa';

        const modal = document.getElementById('eventModal');
        modal.classList.remove('hidden'); modal.classList.add('show');
    }

    function criarFiltros(panelList) {
        // ===== FILTROS =====
        const filterContainer = document.createElement('div');
        filterContainer.className = 'filter-container flex gap-2 mb-2 items-center';

        // Filtro de prioridade
        const prioridadeSelect = document.createElement('select');
        prioridadeSelect.innerHTML = `
            <option value="">Todas prioridades</option>
            <option value="alta">Alta</option>
            <option value="media">Média</option>
            <option value="baixa">Baixa</option>
        `;
        prioridadeSelect.className = 'border rounded px-2 py-1';
        filterContainer.appendChild(prioridadeSelect);

        // Filtro de mês
        const monthSelect = document.createElement('select');
        MONTH_NAMES.forEach((nome, idx) => {
            const option = document.createElement('option');
            option.value = idx;
            option.textContent = nome;
            if (idx === currentMonth) option.selected = true;
            monthSelect.appendChild(option);
        });
        monthSelect.className = 'border rounded px-2 py-1';
        filterContainer.appendChild(monthSelect);

        panelList.appendChild(filterContainer);

        return { prioridadeSelect, monthSelect };
    }

    function atualizarPainelTodosEventos(eventos) {
        const panelList = document.getElementById('eventsPanelList');
        if (!panelList) return;

        // Remove apenas os eventos, não os filtros
        panelList.querySelectorAll('.event-item, .no-events').forEach(e => e.remove());

        // Se os filtros ainda não existem, cria uma vez
        let prioridadeSelect = panelList.querySelector('select:first-child');
        let monthSelect = panelList.querySelector('select:last-child');

        if (!prioridadeSelect || !monthSelect) {
            const filtros = criarFiltros(panelList);
            prioridadeSelect = filtros.prioridadeSelect;
            monthSelect = filtros.monthSelect;

            // Listeners para filtros
            prioridadeSelect.addEventListener('change', () => renderizarEventosFiltrados(eventos, prioridadeSelect, monthSelect, panelList));
            monthSelect.addEventListener('change', () => renderizarEventosFiltrados(eventos, prioridadeSelect, monthSelect, panelList));
        }

        // Renderiza os eventos filtrados
        renderizarEventosFiltrados(eventos, prioridadeSelect, monthSelect, panelList);
    }

    function renderizarEventosFiltrados(eventos, prioridadeSelect, monthSelect, panelList) {
        // Remove eventos já renderizados
        panelList.querySelectorAll('.event-item, .no-events').forEach(e => e.remove());

        const selectedPriority = prioridadeSelect.value;
        const selectedMonth = parseInt(monthSelect.value, 10);

        const filtered = eventos.filter(ev => {
            const evMonth = new Date(ev.data).getMonth();
            const matchMonth = evMonth === selectedMonth;
            const matchPriority = selectedPriority ? ev.prioridade === selectedPriority : true;
            return matchMonth && matchPriority;
        });

        if (filtered.length === 0) {
            const p = document.createElement('p');
            p.className = 'text-gray-600 no-events';
            p.textContent = 'Nenhum evento encontrado.';
            panelList.appendChild(p);
            return;
        }

        filtered.forEach(ev => {
            const div = document.createElement('div');
            div.className = 'event-item flex justify-between items-center my-1 p-1 border rounded';

            const dataFormatada = new Date(ev.data).toLocaleDateString();

            div.innerHTML = `
                <span>
                    <span style="color:${EVENT_COLORS[ev.prioridade] || '#3f6bff'}">●</span>
                    ${dataFormatada} ${ev.horaInicio || ''} - ${ev.titulo}
                </span>
                <div class="flex gap-2">
                    <button class="editEventBtn bg-blue-500 hover:bg-blue-600 text-white px-2 py-1 rounded text-sm">Editar</button>
                    <button class="deleteEventBtn bg-red-600 hover:bg-red-700 text-white px-2 py-1 rounded text-sm">Excluir</button>
                </div>
            `;

            // Editar evento
            div.querySelector('.editEventBtn').addEventListener('click', () => abrirModalEvento(ev));

            // Excluir evento
            div.querySelector('.deleteEventBtn').addEventListener('click', async () => {
                if (!confirm(`Deseja realmente excluir o evento "${ev.titulo}"?`)) return;
                try {
                    const res = await fetch(`${EVENT_API}/${ev.id}`, { method:'DELETE', credentials:'include' });
                    if (!res.ok) throw new Error();
                    await carregarEventosMes(currentMonth, currentYear);
                    eventos = eventosMes; // atualiza lista de eventos
                    renderizarEventosFiltrados(eventos, prioridadeSelect, monthSelect, panelList);
                    renderCalendar(currentMonth, currentYear);
                } catch(err) {
                    console.error(err);
                    alert('Erro ao excluir evento.');
                }
            });

            panelList.appendChild(div);
        });
    }

    // ===========================
    // INICIALIZAÇÃO
    // ===========================
    document.addEventListener('DOMContentLoaded', async () => {
        await carregarUsuarioLogado();
        renderCalendar(currentMonth, currentYear);

        const modal = document.getElementById('eventModal');
        const eventsPanel = document.getElementById('eventsPanel');
        const rightPanel = document.getElementById('rightPanel');

        document.getElementById('createEventBtn')?.addEventListener('click', (e) => {
            e.preventDefault(); abrirModalEvento();
        });

        document.getElementById('closeRightPanelBtn')?.addEventListener('click', () => {
            rightPanel?.classList.add('hidden');
        });

        document.getElementById('closeModalBtn')?.addEventListener('click', () => {
            modal.classList.remove('show'); modal.classList.add('hidden');
            limparModal();
        });

        // BOTÃO MOSTRAR TODOS OS EVENTOS
        document.getElementById('showEventsBtn')?.addEventListener('click', async () => {
            if (!eventsPanel) return;
            await carregarEventosMes(currentMonth, currentYear);
            atualizarPainelTodosEventos(eventosMes);

            eventsPanel.classList.remove('hidden'); eventsPanel.classList.add('show');
            if (rightPanel) { rightPanel.classList.remove('show'); rightPanel.classList.add('hidden'); }
            document.querySelectorAll('.day').forEach(d => d.classList.remove('selected'));
        });

        document.getElementById('closePainelBtn')?.addEventListener('click', () => {
            eventsPanel.classList.remove('show'); eventsPanel.classList.add('hidden');
        });

        // FORMULÁRIO DE EVENTO
        const form = document.getElementById('form-evento');
        form?.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            const titulo = document.getElementById('eventTitle').value.trim();
            const descricao = document.getElementById('eventDescription').value.trim();
            const data = document.getElementById('eventDate').value;
            const horaInicio = document.getElementById('eventStartTime').value;
            const horaFim = document.getElementById('eventEndTime').value;
            const local = document.getElementById('eventLocation').value.trim();
            const prioridade = document.getElementById('eventPriority').value;
            const id = document.getElementById('eventId').value;

            if (!titulo || !data) { alert("Título e data são obrigatórios."); return; }

            const payload = { titulo, descricao, data, horaInicio, horaFim, local, prioridade, usuarioId: usuarioLogado?.id };

            try {
                const method = id ? 'PUT' : 'POST';
                const url = id ? `${EVENT_API}/${id}` : EVENT_API;
                const res = await fetch(url, { method, body: JSON.stringify(payload), headers: {'Content-Type':'application/json'}, credentials:'include' });
                if (!res.ok) throw new Error('Erro ao salvar evento');

                modal.classList.remove('show'); modal.classList.add('hidden');
                limparModal();
                await renderCalendar(currentMonth, currentYear);
            } catch (err) { console.error(err); alert("Erro ao salvar evento."); }
        });

        document.getElementById('deleteEventBtn')?.addEventListener('click', async () => {
            const id = document.getElementById('eventId').value;
            if (!id) return;
            if (!confirm('Deseja realmente excluir o evento?')) return;
            try {
                const res = await fetch(`${EVENT_API}/${id}`, { method:'DELETE', credentials:'include' });
                if (!res.ok) throw new Error();
                modal.classList.remove('show'); modal.classList.add('hidden');
                limparModal();
                await renderCalendar(currentMonth, currentYear);
            } catch(err){ console.error(err); alert('Erro ao excluir'); }
        });
    });

})();
