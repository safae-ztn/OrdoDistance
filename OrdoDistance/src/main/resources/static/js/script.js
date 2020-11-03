$(function(){
    
$('.medicaments-space button').click(function(){
    $('.medicaments').append('<div class="medicament-info"><section><label for="">Medicament : </label><input type="text" name="nom" id="" class="medicament-input" placeholder="Nom du medicament"></section><section><label for="">Nombre de fois : </label><input type="text" name="nbrefois" id="" class="nbrDeFois-input" placeholder="Ex : 2 xj"></section> <section><label for="">Pendant : </label><input type="text" name="pendant" id="" class="pendant-input" placeholder="Ex: 1 semaine"></section></div>');
    return false;    
});
});
