const messagesEl = document.getElementById('messages');
const form = document.getElementById('ask-form');
const input = document.getElementById('question');
const sendBtn = document.getElementById('send');
const newChatBtn = document.getElementById('new-chat');
const reingestBtn = document.getElementById('reingest');

const SID_KEY = 'ragai-sid';

let sid = localStorage.getItem(SID_KEY);
if (!sid) {
	sid = crypto.randomUUID();
	localStorage.setItem(SID_KEY, sid);
}

function addMessage(role, text, sources = []) {
	const msg = document.createElement('div');
	msg.className = `msg ${role}`;

	const bubble = document.createElement('div');
	bubble.className = 'bubble';
	bubble.textContent = text;
	msg.appendChild(bubble);

	if (sources.length > 0) {
		const chips = document.createElement('div');
		chips.className = 'sources';
		for (const source of sources) {
			const chip = document.createElement('span');
			chip.className = 'chip';
			chip.textContent = source;
			chips.appendChild(chip);
		}
		msg.appendChild(chips);
	}

	messagesEl.appendChild(msg);
	messagesEl.scrollTop = messagesEl.scrollHeight;
	return msg;
}

function addTyping() {
	const msg = addMessage('assistant', '');
	const bubble = msg.querySelector('.bubble');
	bubble.className = 'bubble typing';
	for (let i = 0; i < 3; i++) {
		const dot = document.createElement('span');
		bubble.appendChild(dot);
	}
	return msg;
}

async function ask(question) {
	addMessage('user', question);
	const typing = addTyping();
	sendBtn.disabled = true;

	try {
		const url = `/chat?q=${encodeURIComponent(question)}&sid=${encodeURIComponent(sid)}`;
		const res = await fetch(url);
		if (!res.ok) {
			throw new Error(`HTTP ${res.status}`);
		}
		const data = await res.json();
		if (data.sessionId) {
			sid = data.sessionId;
			localStorage.setItem(SID_KEY, sid);
		}
		typing.remove();
		addMessage('assistant', data.answer, data.sources || []);
	} catch (err) {
		typing.remove();
		addMessage('assistant', `Error: ${err.message}. Is the server running?`);
	} finally {
		sendBtn.disabled = false;
		input.focus();
	}
}

form.addEventListener('submit', (e) => {
	e.preventDefault();
	const question = input.value.trim();
	if (!question || sendBtn.disabled) {
		return;
	}
	input.value = '';
	ask(question);
});

document.querySelectorAll('.suggest .chip').forEach((chip) => {
	chip.addEventListener('click', () => {
		input.value = chip.textContent;
		form.requestSubmit();
	});
});

newChatBtn.addEventListener('click', () => {
	sid = crypto.randomUUID();
	localStorage.setItem(SID_KEY, sid);
	messagesEl.innerHTML = '';
	input.focus();
});

reingestBtn.addEventListener('click', async () => {
	reingestBtn.disabled = true;
	try {
		const res = await fetch('/ingest', { method: 'POST' });
		if (!res.ok) {
			throw new Error(`HTTP ${res.status}`);
		}
		const text = await res.text();
		addMessage('system', text);
	} catch (err) {
		addMessage('system', `Re-ingest failed: ${err.message}`);
	} finally {
		reingestBtn.disabled = false;
	}
});
