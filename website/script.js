const sendForm = document.getElementById('send-form');
const receiver = document.getElementById('receiver');
const sender = document.getElementById('sender');
const input = document.getElementById('input');
const output = document.getElementById('output');

const API_URL = 'https://iakcukzcdqfkwkjyhmic.supabase.co/rest/v1';
const API_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlha2N1a3pjZHFma3dranlobWljIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NjkzMDQ5MjksImV4cCI6MTk4NDg4MDkyOX0.jyl6BeUXfy065ofN8ZJi5knUeSknw7yoPQddonoavew';

async function fetchDevices() {
    const response = await fetch(`${API_URL}/devices?select=*,device_messages(*)`, {
        headers: {
            apikey: API_KEY,
            Authorization: `Bearer ${API_KEY}`
        }
    });
    const devices = await response.json();
    receiver.innerHTML = devices.map(device => `<option value="${device.id}">${device.name} (${device.device_messages.length} message${device.device_messages.length == 1 ? '' : 's'})</option>`).join('');
}

const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ,.!? \n'.split('');
const hieroglyphs = ['𓃓', '𓁵', '𓁄', '𓅓', '𓀩', '𓀛', '𓁳', '𓀎', '𓀰', '𓀱', '𓀌', '𓄇', '𓀨', '𓀀', '𓁤', '𓁱', '𓅦', '𓁬', '𓁺', '𓀛', '𓅂', '𓄿', '𓅀', '𓀁', '𓆦', '𓋔', '𓅻', '𓊿', '𓉮', '𓆣', '𓃻', '𓆡'];
const specials = {
    '0': 'nul', '1': 'een', '2': 'twee', '3': 'drie', '4': 'vier', '5': 'vijf', '6': 'zes', '7': 'zeven', '8': 'acht', '9': 'negen',
    '@': 'at', ':': 'dubbelepunt', '/': 'slash', '\\': 'backslash', '#': 'hashtag', '&': 'en',
    '+': 'plus', '-': 'min', '=': 'is'
};

function rand(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function encode() {
    const chars = [...input.value.toUpperCase()];
    if (chars.length == 0) {
        output.value = '';
        return;
    }

    const shift = rand(1, 32);
    let encoded = hieroglyphs[shift - 1];
    for (let i = 0; i < chars.length; i++) {
        if (chars[i] in specials) {
            if (i > 0 && chars[i - 1] != ' ') {
                encoded += hieroglyphs[(letters.indexOf(' ') + shift) & 31];
            }
            const specialChars = [...specials[chars[i]].toUpperCase()];
            for (let j = 0; j < specialChars.length; j++) {
                encoded += hieroglyphs[(letters.indexOf(specialChars[j]) + shift) & 31];
            }
            if (i < chars.length && chars[i + 1] != ' ') {
                encoded += hieroglyphs[(letters.indexOf(' ') + shift) & 31];
            }
        } else {
            let index = letters.indexOf(chars[i]) != -1 ? letters.indexOf(chars[i]) : letters.indexOf('?');
            encoded += hieroglyphs[(index + shift) & 31];
        }
    }
    output.value = encoded;
}
input.addEventListener('input', encode);

function decode() {
    const chars = [...output.value];
    const shift = hieroglyphs.indexOf(chars[0]) + 1;
    let decoded = '';
    for (let i = 1; i < chars.length; i++) {
        const index = hieroglyphs.indexOf(chars[i]);
        if (index != -1) {
            decoded += letters[(index - shift) & 31];
        }
    }
    input.value = decoded;
}
output.addEventListener('input', decode);

sendForm.addEventListener('submit', async event => {
    event.preventDefault();

    await fetch(`${API_URL}/device_messages`, {
        method: 'POST',
        headers: {
            apikey: API_KEY,
            Authorization: `Bearer ${API_KEY}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            device_id: receiver.value,
            sender: sender.value,
            message: output.value
        })
    });

    input.value = '';
    output.value = '';
    fetchDevices();
});

fetchDevices();
encode();
