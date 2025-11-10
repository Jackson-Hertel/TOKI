const monthYear = document.getElementById('month-year');
const calendar = document.getElementById('calendar');
const prevBtn = document.getElementById('prev');
const nextBtn = document.getElementById('next');
const nomeUsuario = document.getElementById('nomeUsuario');

let currentDate = new Date();

function renderCalendar(date) {
  calendar.innerHTML = '';

  const year = date.getFullYear();
  const month = date.getMonth();

  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const firstDayWeek = firstDay.getDay();

  const months = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ];

  monthYear.textContent = `${months[month]} ${year}`;

  // Espaços antes do primeiro dia
  for (let i = 0; i < firstDayWeek; i++) {
    const emptyDiv = document.createElement('div');
    calendar.appendChild(emptyDiv);
  }

  // Dias do mês
  for (let day = 1; day <= lastDay.getDate(); day++) {
    const dayDiv = document.createElement('div');
    dayDiv.classList.add('day');
    dayDiv.textContent = day;

    const today = new Date();
    if (
      day === today.getDate() &&
      month === today.getMonth() &&
      year === today.getFullYear()
    ) {
      dayDiv.classList.add('today');
    }

    calendar.appendChild(dayDiv);
  }
}

// Navegação de meses
prevBtn.addEventListener('click', () => {
  currentDate.setMonth(currentDate.getMonth() - 1);
  renderCalendar(currentDate);
});

nextBtn.addEventListener('click', () => {
  currentDate.setMonth(currentDate.getMonth() + 1);
  renderCalendar(currentDate);
});

// Nome do usuário (exemplo)
const usuarioSalvo = localStorage.getItem('usuarioNome');
if (usuarioSalvo) {
  nomeUsuario.textContent = `Olá, ${usuarioSalvo}!`;
} else {
  nomeUsuario.textContent = 'Bem-vindo!';
}

renderCalendar(currentDate);
