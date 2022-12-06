
function color(element) {
    document.getElementById(element).style.opacity = 1;
}
function hide(element) {
    document.getElementById(element).style.opacity = 0;
}
function PlaySound(soundobj) {
    var thissound=document.getElementById(soundobj);
    thissound.play();
}

function StopSound(soundobj) {
    var thissound=document.getElementById(soundobj);
    thissound.pause();
    thissound.currentTime = 0;
}
function colorzj() {
	color('zj');
    PlaySound('bgmzj');
}

function colorxz() {
	color('xz');
    PlaySound('bgmxz');
}

function colornj() {
	color('nj');
    PlaySound('bgmnj');
}

function colorberk() {
	color('berk');
    PlaySound('bgmberk');
}

function colornext() {
	color('next');
    document.getElementById("bgmnext").play();
}

document.getElementById("placeholder1").onmouseover = colorzj;
document.getElementById("placeholder2").onmouseover = colorxz;
document.getElementById("placeholder3").onmouseover = colornj;
document.getElementById("placeholder4").onmouseover = colorberk;
document.getElementById("placeholder5").onmouseover = colornext;


function hidezj() {
	hide('zj');
    StopSound('bgmzj');
}

function hidexz() {
	hide('xz');
    StopSound('bgmxz');
}

function hidenj() {
	hide('nj');
    StopSound('bgmnj');
}

function hideberk() {
	hide('berk');
    StopSound('bgmberk');
}

function hidenext() {
	hide('next');
}

document.getElementById("placeholder1").onmouseout = hidezj;
document.getElementById("placeholder2").onmouseout = hidexz;
document.getElementById("placeholder3").onmouseout = hidenj;
document.getElementById("placeholder4").onmouseout = hideberk;
document.getElementById("placeholder5").onmouseout = hidenext;


// document.getElementById("background-music").play();








function unhideLightbox(lightboxID) {
   document.getElementById('lightbox-overlay').classList.remove('hidden');
   document.getElementById(lightboxID).classList.remove("hidden");
}


function unhideLightbox1() {
   unhideLightbox('childhood');
}

document.getElementById("placeholder1").onclick = function () {
   unhideLightbox("childhood");
};

document.getElementById("placeholder2").onclick = function () {
   unhideLightbox("vernacular");
};

document.getElementById("placeholder3").onclick = function () {
   unhideLightbox("urban");
};

document.getElementById("placeholder4").onclick = function () {
    unhideLightbox("dancing");
 };


function closeLightbox(lightboxID) {
   document.getElementById('lightbox-overlay').classList.add('hidden');
   document.getElementById(lightboxID).classList.add("hidden");
}



function closeAllLightboxes() {
   var lightboxElements = document.getElementsByClassName('lightbox');
   for (var i = 0; i < lightboxElements.length; i++) {
       var id = lightboxElements[i].id;
       closeLightbox(id);
   }
}

document.getElementById("lightbox-overlay").onclick = function () {
   closeAllLightboxes();
 };

 $(document).ready(function() {
    $("#name").typeIt({
        speed: 60,
        loop: false
    })

    .tiType("Hi, I'm Angela")
    .tiPause(800)
    .tiDelete(14)


    .tiPause(800)
    .tiType("I'm a cogsci/cs major")
    .tiPause(800)
    .tiDelete(16)


    .tiPause(800)
    .tiType("n aspiring UI/UX designer")
    .tiPause(800)
    .tiDelete(25)

    .tiPause(800)
    .tiType(" part-time vegan")
    .tiPause(800)
    .tiDelete(16)

    .tiPause(800)
    .tiType("n occasional food blogger")
    .tiPause(800)
    .tiDelete(23)

    .tiPause(800)
    .tiType("experienced dog-sitter")
    .tiPause(800)
    .tiDelete(30)

    .tiType("Hi, I'm Angela")
    .tiPause(1000)
});

    