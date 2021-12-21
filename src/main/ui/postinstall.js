const fs = require('fs');

fs.readFile('node_modules/ical.js/build/ical.js', 'utf8', (err, data) => {
    if (err) {
        throw err;
    } else {
        fs.writeFile('node_modules/ical.js/build/ical.js', "let ICAL;\n" + data, (err) => {
            if (err) throw err;
        });
    }
});
