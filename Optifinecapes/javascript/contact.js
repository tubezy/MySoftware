document.addEventListener('click', (event) => {
    if (event.target.closest('.text')) {
        var content = event.target.textContent;
        var el = document.createElement('textarea');
        el.value = content;
        el.setAttribute('readonly', '');
        el.style = { position: 'absolute', left: '-9999px' };
        document.body.appendChild(el);
        el.select();
        document.execCommand('copy');
        document.body.removeChild(el);
        document.getElementById("notification").style.visibility = "visible";
    }

     else if (event.target.closest('.notification')) {
        document.getElementById("notification").style.visibility = "hidden";
     }
});