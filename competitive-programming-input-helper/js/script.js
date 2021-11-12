(function ($, _) {
  "use strict"; // Start of use strict

  const formatData = (input) => {
    let payload = {
      interactive: false,
      memoryLimit: 256,
      timeLimit: 1500,
      tests: [],
      testType: "single",
      input: {
        type: "stdin",
      },
      output: {
        type: "stdout",
      },
      languages: {
        java: {
          mainClass: "Main",
        },
      },
    };
    payload["name"] = input["name"];
    payload["url"] = input["url"];
    const targetKeys = _.keys(input)
      .filter((key) => key.startsWith("input-"))
      .map((key) => key.replace("input-", ""));

    targetKeys.forEach((key) => {
      payload["tests"].push({
        input: input[`input-${key}`],
        output: input[`output-${key}`],
      });
    });
    const className = _.words(payload["name"], /[A-Za-z0-9_]+/g)
      .map(_.upperFirst)
      .join("")
      .trim();
    payload["languages"]["java"]["taskClass"] = className;
    return payload;
  };

  const doPostRequest = (rawData) => {
    const payload = formatData(rawData);
    console.log("payload", payload);
    $.ajax({
      "url": `http://localhost:${window.location.port}`,
      "method": "POST",
      "headers": {
        "Content-Type": "application/json"
      },
      "data": JSON.stringify(payload),
    }).done((response) => {
      console.log("response", response);
    });
  };

  const testCases = [];
  const addInputTitle = function () {
    const id = Math.random().toString(36).substring(7);
    testCases.push(id);
    const template = `
    <div class="card clearfix" data-id="${id}">
      <div class="card-header" data-toggle="collapse" data-target="#collapse${id}">
        <a href="#" class="float-left case-link">Test Case #${testCases.length}</a>
        <button data-target="${id}" type="button" class="border-0 px-2 py-1 btn btn-danger float-right remove-input">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
            <path fill="none" d="M0 0h24v24H0z" />
            <path d="M5 11h14v2H5z" fill="rgba(255,255,255,1)" />
          </svg>
        </button>
      </div>
      <div id="collapse${id}" class="collapse show">
        <div class="card-body">
          <div class="row">
            <div class="col form-group required">
              <label for="input" class="control-label">Input</label>
              <textarea id="input${id}" name="input-${id}" cols="40" rows="5" class="form-control"></textarea>
            </div>
            <div class="col form-group required">
              <label for="output" class="control-label">Output</label>
              <textarea id="output${id}" name="output-${id}" cols="40" rows="5" class="form-control"></textarea>
            </div>
          </div>
        </div>
      </div>
    </div>
    `;
    $("#input-accordion").append(template);
  };

  // Input 1
  addInputTitle();

  // Add  Test Case
  $("#addInput").click((event) => {
    addInputTitle();
  });

  // Remove Test Case
  $("#input-accordion").on("click", ".remove-input", (event) => {
    event.stopPropagation();
    const target = $(event.currentTarget).data("target");
    $(event.delegateTarget).find(`.card[data-id=${target}]`).remove();
    $(event.delegateTarget)
      .find(".card")
      .each(function (index) {
        $(this)
          .find(".case-link")
          .text(`Test Case #${index + 1}`);
      });
    _.remove(testCases, (i) => i === target);
  });

  function getFormData($form) {
    var un_indexed_array = $form.serializeArray();
    var indexed_array = {};
    $.map(un_indexed_array, function (n, i) {
      indexed_array[n["name"]] = n["value"];
    });
    return indexed_array;
  }

  $("#input-form").submit(function (event) {
    event.preventDefault();
    const values = getFormData($(this));
    doPostRequest(values);
  });
})(jQuery, _); // End of use strict
