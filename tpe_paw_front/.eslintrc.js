module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: [
    'plugin:vue/essential',
    '@vue/standard'
  ],
  parserOptions: {
    parser: 'babel-eslint',
    emitWarning: true,
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    "camelcase": 1, 
    "indent": 0,
    "no-trailing-spaces": 0,
    "semi": 0,
    "space-before-function-paren": 1,
    "prefer-const": 1,
    "space-before-blocks": 1,
    "space-infix-ops": 1,
    "comma-dangle": 1,
    "no-cond-assign": 1,
    "no-console": 1,
    "no-constant-condition": 1,
    "no-control-regex": 1,
    "no-debugger": 1,
    "no-dupe-args": 1,
    "no-dupe-keys": 1,
    "no-duplicate-case": 1,
    "no-empty-character-class": 1,
    "no-empty": 1,
    "no-ex-assign": 1,
    "no-extra-boolean-cast": 1,
    "no-extra-semi": 1,
    "no-func-assign": 1,
    "no-inner-declarations": 1,
    "no-invalid-regexp": 1,
    "no-irregular-whitespace": 1,
    "no-negated-in-lhs": 1,
    "no-obj-calls": 1,
    "no-regex-spaces": 1,
    "no-sparse-arrays": 1,
    "no-unreachable": 1,
    "use-isnan": 1,
    "valid-typeof": 1,
    "no-fallthrough": 1,
    "no-octal": 1,
    "no-redeclare": 1,
    "no-delete-var": 1,
    "no-undef": 1,
    "no-unused-vars": 1,
    "no-mixed-spaces-and-tabs": 1,

  },
  overrides: [
    {
      files: [
        '**/__tests__/*.{j,t}s?(x)',
        '**/tests/unit/**/*.spec.{j,t}s?(x)'
      ],
      env: {
        mocha: true
      }
    }
  ]
}
