// Check if a new cache is available on page load.
window.addEventListener('load', function(e) {
  window.applicationCache.addEventListener('updateready', function(e) {
    if (window.applicationCache.status == window.applicationCache.UPDATEREADY) {
      // Browser downloaded a new app cache.
      var offline_messages = document.getElementById('offline-messages');
      offline_messages.innerHTML = 'Het programma werd aangepast!';
      offline_messages.style.display = "block";
      setTimeout(function(){
        offline_messages.style.display = "none";
      },5000);
    }
  }, false);

  window.applicationCache.addEventListener('cached', function(e) {
    if (window.applicationCache.status == window.applicationCache.IDLE) {
      // Browser downloaded the manifest for the first time.
      var offline_messages = document.getElementById('offline-messages');
      offline_messages.innerHTML = 'Het programma is nu offline beschikbaar!';
      offline_messages.style.display = "block";
      setTimeout(function(){
        offline_messages.style.display = "none";
      },5000);
    }
  }, false);
}, false);

(function ($) {
  "use strict";
  $(document).ready(function() {
    $('.s-t').on('click', function(e) {
      $(this).toggleClass('js-open').find('.e-desc').slideToggle();
    });
  });
})(jQuery);
